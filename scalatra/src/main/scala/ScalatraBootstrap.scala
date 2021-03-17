import com.mirae.mp3loud._
import com.typesafe.config.ConfigFactory
import org.scalatra._
import slick.jdbc.JdbcBackend.Database

import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  val isEb = System.getenv("IS_EB")
  val conf = ConfigFactory.parseResources("application.conf")

  val serviceDb = isEb match {
    case "1" => {
      Database.forURL(
        conf.getString("RDSPostgres.url"),
        conf.getString("RDSPostgres.user"),
        conf.getString("RDSPostgres.password")
      )
    }
    case _ => {
      Database.forURL(
        conf.getString("localPostgres2.url"),
        conf.getString("localPostgres2.user"),
        conf.getString("localPostgres2.password")
      )
    }
  }

  override def init(context: ServletContext) {
    context.mount(new MyScalatraServlet(serviceDb), "/*")
  }
}
