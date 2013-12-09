package net.kamekoopa.play.plugin.aws.s3

import com.amazonaws.{ClientConfiguration, Protocol}
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import play.api.{PlayException, Configuration}


/** Playの設定から構築したプラグインの設定オブジェクト
  * @see [[com.amazonaws.ClientConfiguration]]
  */
class Config private (
  val accessKey: String,
  val secretKey: String,
  val protocol: Protocol,
  val proxyHost: Option[String] = None,
  val proxyPort: Option[Int] = None,
  val connectionTimeout: Option[Int] = None,
  val maxConnections: Option[Int] = None,
  val maxErrorRetry: Option[Int] = None,
  val proxyDomain: Option[String] = None,
  val proxyPassword: Option[String] = None,
  val proxyUsername: Option[String] = None,
  val proxyWorkstation: Option[String] = None,
  val socketTimeout: Option[Int] = None,
  val userAgent: Option[String] = None,
  val useReaper: Option[Boolean] = None
) {

  val credential = new BasicAWSCredentials(accessKey, secretKey)

  private val _clientConfig = {

    val config = new ClientConfiguration()
    config.setProtocol(protocol)

    proxyHost.foreach(config.setProxyHost)
    proxyPort.foreach(config.setProxyPort)
    connectionTimeout.foreach(config.setConnectionTimeout)
    maxConnections.foreach(config.setMaxConnections)
    maxErrorRetry.foreach(config.setMaxErrorRetry)
    proxyDomain.foreach(config.setProxyDomain)
    proxyPassword.foreach(config.setProxyPassword)
    proxyUsername.foreach(config.setProxyUsername)
    proxyWorkstation.foreach(config.setProxyWorkstation)
    socketTimeout.foreach(config.setSocketTimeout)
    userAgent.foreach(config.setUserAgent)
    useReaper.foreach(config.setUseReaper)

    config
  }

  val clientConfig = new ClientConfiguration(_clientConfig)

  /** この設定からS3のクライアントオブジェクトを生成します
    *
    * @return S3クライアント
    */
  def createS3Client: AmazonS3Client = new AmazonS3Client(credential, _clientConfig)

}

/** コンパニオンオブジェクト */
object Config {

  /** プロトコル設定として許可される文字列 */
  private lazy val protocols = Some(Set("HTTP", "HTTPS"))

  /** 設定オブジェクトを構築します。
    * 設定オブジェクト構築に必要な情報が足りないかplugin無効設定がされている場合はNoneを返します
    *
    * @param playConf Playの設定
    * @return 設定オブジェクト
    */
  def apply(playConf: Configuration): Option[Config] = {

    //s3.enabled = trueかつcredentialが取得できる場合
    //aws.*** より aws.s3.***の方が優先する
    for {
      enabled <- playConf.getBoolean("aws.s3.enabled") if enabled
      accessKey <- playConf.getString("aws.accessKey").orElse{playConf.getString("aws.s3.accessKey")}
      secretKey <- playConf.getString("aws.secretKey").orElse{playConf.getString("aws.s3.secretKey")}
    } yield {

      //デフォルトプロトコル、あるいは不明なプロトコル指定はHTTPSとして扱う
      val protocol = try {
        playConf
          .getString("aws.protocol", protocols).orElse({playConf.getString("aws.s3.protocol", protocols)})
          .map(Protocol.valueOf)
          .getOrElse(Protocol.HTTPS)
      } catch {
        case e: PlayException => Protocol.HTTPS
        case e: Throwable => throw e
      }

      val proxyHost = playConf.getString("aws.proxyHost").orElse({playConf.getString("aws.s3.proxyHost")})
      val proxyPort = playConf.getInt("aws.proxyPort").orElse({playConf.getInt("aws.s3.proxyPort")})
      val connectionTimeout = playConf.getMilliseconds("aws.connectionTimeout").orElse({playConf.getMilliseconds("aws.s3.connectionTimeout")}).map(_.toInt)
      val maxConnections = playConf.getInt("aws.maxConnections").orElse({playConf.getInt("aws.s3.maxConnections")})
      val maxErrorRetry = playConf.getInt("aws.maxErrorRetry").orElse({playConf.getInt("aws.s3.maxErrorRetry")})
      val proxyDomain = playConf.getString("aws.proxyDomain").orElse({playConf.getString("aws.s3.proxyDomain")})
      val proxyPassword = playConf.getString("aws.proxyPassword").orElse({playConf.getString("aws.s3.proxyPassword")})
      val proxyUsername = playConf.getString("aws.proxyUsername").orElse({playConf.getString("aws.s3.proxyUsername")})
      val proxyWorkstation = playConf.getString("aws.proxyWorkstation").orElse({playConf.getString("aws.s3.proxyWorkstation")})
      val socketTimeout = playConf.getMilliseconds("aws.socketTimeout").orElse({playConf.getMilliseconds("aws.s3.socketTimeout")}).map(_.toInt)
      val userAgent = playConf.getString("aws.userAgent").orElse({playConf.getString("aws.s3.userAgent")})
      val useReaper = playConf.getBoolean("aws.useReaper").orElse({playConf.getBoolean("aws.s3.useReaper")})

      new Config(
        accessKey,
        secretKey,
        protocol,
        proxyHost,
        proxyPort,
        connectionTimeout,
        maxConnections,
        maxErrorRetry,
        proxyDomain,
        proxyPassword,
        proxyUsername,
        proxyWorkstation,
        socketTimeout,
        userAgent,
        useReaper
      )
    }
  }
}