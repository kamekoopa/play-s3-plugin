package net.kamekoopa.play.plugin.aws.s3

import com.amazonaws.{ClientConfiguration, Protocol}
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import play.api.{PlayException, Configuration}


/** Playの設定から構築したプラグインの設定オブジェクト
  *
  * @param accessKey アクセスキー
  * @param secretKey アクセスシークレット
  * @param protocol プロトコル
  * @param proxyHost プロキシホスト
  * @param proxyPort プロキシポート
  */
class Config private (
  val accessKey: String,
  val secretKey: String,
  val protocol: Protocol,
  val proxyHost: Option[String] = None,
  val proxyPort: Option[Int] = None
) {

  /** この設定からS3のクライアントオブジェクトを生成します
    *
    * @return S3クライアント
    */
  def createS3Client: AmazonS3Client = {

    val credential = new BasicAWSCredentials(accessKey, secretKey)

    val config = new ClientConfiguration()
    config.setProtocol(protocol)

    proxyHost.foreach(config.setProxyHost)
    proxyPort.foreach(config.setProxyPort)

    new AmazonS3Client(credential, config)
  }
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

      new Config(
        accessKey,
        secretKey,
        protocol,
        proxyHost,
        proxyPort
      )
    }
  }
}