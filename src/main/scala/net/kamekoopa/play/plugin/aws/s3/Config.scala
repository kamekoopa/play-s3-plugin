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
      accessKey <- get("accessKey", playConf.getString(_: String))
      secretKey <- get("secretKey", playConf.getString(_: String))
    } yield {

      //デフォルトプロトコル、あるいは不明なプロトコル指定はHTTPSとして扱う
      val protocol = try {
        get("protocol", playConf.getString(_: String, protocols))
          .map(Protocol.valueOf)
          .getOrElse(Protocol.HTTPS)
      } catch {
        case e: PlayException => Protocol.HTTPS
        case e: Throwable => throw e
      }

      val proxyHost = get("proxyHost", playConf.getString(_: String))
      val proxyPort = get("proxyPort", playConf.getInt)
      val connectionTimeout = get("connectionTimeout", playConf.getMilliseconds).map(_.toInt)
      val maxConnections = get("maxConnections", playConf.getInt)
      val maxErrorRetry = get("maxErrorRetry", playConf.getInt)
      val proxyDomain = get("proxyDomain", playConf.getString(_: String))
      val proxyPassword = get("proxyPassword", playConf.getString(_: String))
      val proxyUsername = get("proxyUsername", playConf.getString(_: String))
      val proxyWorkstation = get("proxyWorkstation", playConf.getString(_: String))
      val socketTimeout = get("socketTimeout", playConf.getMilliseconds).map(_.toInt)
      val userAgent = get("userAgent", playConf.getString(_: String))
      val useReaper = get("useReaper", playConf.getBoolean)

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

  /** Playの設定オブジェクトから設定値を取得するヘルパー
    *
    * @param key 設定キー値。aws.$keyにない場合はaws.s3.$keyから取得する
    * @param f キー値から設定値のOptionを取得する関数
    * @tparam T 設定値の型
    * @return 設定値のOption
    */
  private def get[T](key: String, f: String => Option[T]): Option[T] = {
    f(s"aws.$key").orElse({f(s"aws.s3.$key")})
  }
}