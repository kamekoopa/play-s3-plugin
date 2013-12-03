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
  }

  /** Helper */
  def playConf = current.configuration

  /** Helper */
  def fakeApp(kv: (String, Any) *): FakeApplication = {
    FakeApplication(additionalConfiguration = Map(kv: _*))
  }
}
