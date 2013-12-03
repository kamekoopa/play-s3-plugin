package net.kamekoopa.play.plugin.aws.s3

import play.api.Application
import com.amazonaws.services.s3.AmazonS3Client

class Plugin(implicit app: Application) extends play.api.Plugin {

  private val client = Config(app.configuration).map(_.createS3Client)

  override def enabled: Boolean = client.isDefined

  def getClient: AmazonS3Client = client.get
}
