import com.amazonaws.Protocol
import net.kamekoopa.play.plugin.aws.s3.Config
import org.specs2.mutable.Specification
import play.api.test.{WithApplication, FakeApplication}
import play.api.Play.current

class PluginSpec extends Specification {

  "Config" should {

    "設定がなければNone" in new WithApplication(fakeApp()) {

      val config = Config(playConf)

      config must beNone
    }

    "enable=falseならNone" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "false"
    )) {

      val config = Config(playConf)

      config must beNone
    }

    "enable=trueでもaccessKeyとsecretKeyがなければNone" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true"
    )) {

      val config = Config(playConf)

      config must beNone
    }

    "enable=trueでaccessKeyしか指定されていないとNone" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey"
    )) {

      val config = Config(playConf)

      config must beNone
    }

    "enable=trueでsecretKeyしか指定されていないとNone" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.secretKey" -> "secretKey"
    )) {

      val config = Config(playConf)

      config must beNone
    }

    "enable=trueでaccessKeyとsecretKeyが指定されていればSome" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.accessKey mustEqual "accessKey"
      config.get.secretKey mustEqual "secretKey"

    }

    "aws.s3.[accessKey|secretKey]がなければaws.[accessKey|secretKey]を利用する" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.accessKey" -> "accessKey",
      "aws.secretKey" -> "secretKey"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.accessKey mustEqual "accessKey"
      config.get.secretKey mustEqual "secretKey"
    }

    "protocol設定がなければデフォルトでHTTPS" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.protocol mustEqual Protocol.HTTPS
    }

    "protocol設定HTTPSが設定できる" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.protocol" -> "HTTPS"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.protocol mustEqual Protocol.HTTPS
    }

    "protocol設定HTTPが設定できる" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.protocol" -> "HTTP"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.protocol mustEqual Protocol.HTTP
    }

    "protocol設定がHTTPSとHTTP以外ならHTTPSをデフォルト設定にする" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.protocol" -> "HTTTPS"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.protocol mustEqual Protocol.HTTPS
    }

    "aws.s3.protocolがなければaws.protocolを利用する" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.protocol" -> "HTTP"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.protocol mustEqual Protocol.HTTP
    }

    "proxyHost設定がなければNone" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.proxyHost must beNone
    }

    "proxyHost設定が出来る" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.proxyHost" -> "proxyHost"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.proxyHost must beSome
      config.get.proxyHost.get mustEqual "proxyHost"
    }

    "aws.s3.proxyHostがなければaws.proxyHostを利用する" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.proxyHost" -> "proxyHost"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.proxyHost must beSome
      config.get.proxyHost.get mustEqual "proxyHost"
    }

    "proxyPort設定がなければNone" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.proxyPort must beNone
    }

    "proxyPort設定が出来る" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.proxyPort" -> 9000
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.proxyPort must beSome
      config.get.proxyPort.get mustEqual 9000
    }

    "aws.s3.proxyPortがなければaws.proxyPortを利用する" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.proxyPort" -> 9000
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.proxyPort must beSome
      config.get.proxyPort.get mustEqual 9000
    }

    "connectionTimeout設定が出来る" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.connectionTimeout" -> "10s"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.connectionTimeout must beSome
      config.get.connectionTimeout.get mustEqual (10 * 1000)
    }

    "aws.s3.connectionTimeoutがなければaws.connectionTimeoutを利用する" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.connectionTimeout" -> "10s"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.connectionTimeout must beSome
      config.get.connectionTimeout.get mustEqual (10 * 1000)
    }

    "maxConnections設定が出来る" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.maxConnections" -> 10
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.maxConnections must beSome
      config.get.maxConnections.get mustEqual 10
    }

    "aws.s3.maxConnectionsがなければaws.maxConnectionsを利用する" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.maxConnections" -> 10
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.maxConnections must beSome
      config.get.maxConnections.get mustEqual 10
    }

    "maxErrorRetry設定が出来る" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.maxErrorRetry" -> 10
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.maxErrorRetry must beSome
      config.get.maxErrorRetry.get mustEqual 10
    }

    "aws.s3.maxErrorRetryがなければaws.maxErrorRetryを利用する" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.maxErrorRetry" -> 10
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.maxErrorRetry must beSome
      config.get.maxErrorRetry.get mustEqual 10
    }

    "proxyDomain設定が出来る" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.proxyDomain" -> "domain"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.proxyDomain must beSome
      config.get.proxyDomain.get mustEqual "domain"
    }

    "aws.s3.proxyDomainがなければaws.proxyDomainを利用する" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.proxyDomain" -> "domain"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.proxyDomain must beSome
      config.get.proxyDomain.get mustEqual "domain"
    }

    "proxyPassword設定が出来る" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.proxyPassword" -> "pass"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.proxyPassword must beSome
      config.get.proxyPassword.get mustEqual "pass"
    }

    "aws.s3.proxyPasswordがなければaws.proxyPasswordを利用する" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.proxyPassword" -> "pass"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.proxyPassword must beSome
      config.get.proxyPassword.get mustEqual "pass"
    }

    "proxyUsername設定が出来る" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.proxyUsername" -> "user"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.proxyUsername must beSome
      config.get.proxyUsername.get mustEqual "user"
    }

    "aws.s3.proxyUsernameがなければaws.proxyUsernameを利用する" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.proxyUsername" -> "user"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.proxyUsername must beSome
      config.get.proxyUsername.get mustEqual "user"
    }

    "proxyWorkstation設定が出来る" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.proxyWorkstation" -> "work"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.proxyWorkstation must beSome
      config.get.proxyWorkstation.get mustEqual "work"
    }

    "aws.s3.proxyWorkstationがなければaws.proxyWorkstationを利用する" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.proxyWorkstation" -> "work"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.proxyWorkstation must beSome
      config.get.proxyWorkstation.get mustEqual "work"
    }

    "socketTimeout設定が出来る" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.socketTimeout" -> "10s"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.socketTimeout must beSome
      config.get.socketTimeout.get mustEqual (10 * 1000)
    }

    "aws.s3.socketTimeoutがなければaws.socketTimeoutを利用する" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.socketTimeout" -> "10s"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.socketTimeout must beSome
      config.get.socketTimeout.get mustEqual (10 * 1000)
    }

    "userAgent設定が出来る" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.userAgent" -> "agent"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.userAgent must beSome
      config.get.userAgent.get mustEqual "agent"
    }

    "aws.s3.userAgentがなければaws.userAgentを利用する" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.userAgent" -> "agent"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.userAgent must beSome
      config.get.userAgent.get mustEqual "agent"
    }

    "useReaper設定が出来る" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.s3.useReaper" -> "true"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.useReaper must beSome
      config.get.useReaper.get must beTrue
    }

    "aws.s3.useReaperがなければaws.useReaperを利用する" in new WithApplication(fakeApp(
      "aws.s3.enabled" -> "true",
      "aws.s3.accessKey" -> "accessKey",
      "aws.s3.secretKey" -> "secretKey",
      "aws.useReaper" -> "true"
    )) {

      val config = Config(playConf)

      config must beSome
      config.get.useReaper must beSome
      config.get.useReaper.get must beTrue
    }
  }

  /** Helper */
  def playConf = current.configuration

  /** Helper */
  def fakeApp(kv: (String, Any) *): FakeApplication = {
    FakeApplication(additionalConfiguration = Map(kv: _*))
  }
}
