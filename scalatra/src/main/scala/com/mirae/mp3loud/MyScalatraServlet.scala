package com.mirae.mp3loud

import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class MyScalatraServlet (val db: Database) extends ScalatraServlet with JacksonJsonSupport with FutureSupport {
  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  get("/") {
    //views.html.hello()
    new AsyncResult {
      override val is =
        Future {
          contentType = formats("json")
          db.run(sql"select version()".as[String])
        }
    }
  }

}
