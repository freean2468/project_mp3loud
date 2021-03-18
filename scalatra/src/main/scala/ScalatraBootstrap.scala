import com.mirae.mp3loud.route.ServiceRouteServlet
import com.typesafe.config.ConfigFactory
import org.scalatra._
import slick.jdbc.JdbcBackend.Database

import javax.servlet.ServletContext

/** Scala 언어 기반 Web Framework Scalatra를 이용해 mp3 cloud 서비스를 제공하는 웹서버
 *
 * @author 송훈일(sensebe)
 * @since 2021.03.17 ~
 * @version 0.1
 */

class ScalatraBootstrap extends LifeCycle {

  /** Scalatra 앱 실행 시 실행되는 초기화 함수. servlet에 routing class를 mount
   *
   * @param context 현재 servlet context
   */
  override def init(context: ServletContext) {
    /** System.getenv를 통해 현재 서버가 local인지 ec2인지 구분한다.
     *  db 정보는 application.conf 파일에 따로 보관하며 이는 github을 통해 노출하지 않는다.
     */
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
          conf.getString("localPostgres.url"),
          conf.getString("localPostgres.user"),
          conf.getString("localPostgres.password")
        )
      }
    }

    context.mount(new ServiceRouteServlet(serviceDb), "/service")
  }
}
