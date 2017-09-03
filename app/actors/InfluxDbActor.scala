package actors

import javax.inject.Inject

import akka.actor.Actor
import com.paulgoldbaum.influxdbclient.Parameter.Precision
import com.paulgoldbaum.influxdbclient.{InfluxDB, Point}
import play.api.Configuration

import scala.concurrent.ExecutionContext

class InfluxDbActor @Inject()(config: Configuration)(implicit ec: ExecutionContext) extends Actor {

  import InfluxDbActor._

  override def receive: Receive = {
    case Measurement(timestamp, temperature, humidity) =>
      writeToInfluxDb(timestamp, temperature, humidity)
  }

  private def writeToInfluxDb(timestamp: Long, temperature: Float, humidity: Float) = {
    val influxDb = InfluxDB.connect(config.get[String]("app.influxDb.host"), config.get[Int]("app.influxDb.port"))
    val database = influxDb.selectDatabase(config.get[String]("app.influxDb.database"))
    val point = Point("temperature-humidity")
      .addTag("sensor", "sensor1")
      .addField("tmp", temperature)
      .addField("hum", humidity)
    val f = database.write(point, precision = Precision.MILLISECONDS)
    f.onComplete(_ => influxDb.close())
    f
  }
}

object InfluxDbActor {

  case class Measurement(timestamp: Long, temperature: Float, humidity: Float)

}
