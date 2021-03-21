package com.mirae.mp3loud.route

import com.mirae.mp3loud.database.QuerySupport
import com.mirae.mp3loud.database.Tables.{Like, Mp3}
import org.json4s.{DefaultFormats, Formats}
import org.postgresql.util.PSQLException
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.servlet.{FileItem, FileUploadSupport, MultipartConfig, SizeConstraintExceededException}
import org.scalatra.{AsyncResult, BadRequest, FutureSupport, Ok, ScalatraBase, ScalatraServlet}
import org.slf4j.LoggerFactory
import slick.jdbc.JdbcBackend.Database

import java.io.IOException
import scala.concurrent.{ExecutionContext, Future}

/**
 *  ScalatraBase는 Scalatra DSL을 구현해주고
 *  JacksonJsonSupport는 모든 데이터를 Json으로 암묵적 형변환을 구현해주고
 *  FutureSupport는 비동기 응답을 가능케 한다.
 *
 */
trait FileRoute extends ScalatraBase with JacksonJsonSupport with FutureSupport with QuerySupport with FileUploadSupport {
  configureMultipartHandling(MultipartConfig(maxFileSize = Some(10*1024*1024)))
  /** Sets up automatic case class to JSON output serialization, required by the JValueResult trait. */
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  def db: Database

  /** get
   *
   */




  /** post
   *
   */
  post("/upload/:id") {
    val logger = LoggerFactory.getLogger(getClass)
    val genre = params.getOrElse("genre", halt(400))
    val title = params.getOrElse("title", halt(400))
    val artist = params.getOrElse("artist", halt(400))
    val mp3 = fileParams.get("mp3")
    val image = fileParams.get("image")

    mp3 match {
      case None => BadRequest("cannot find file")
      case Some(mp3Bytes) => {
        image match {
          case None => BadRequest("cannot find file")
          case Some(imageBytes) => {
            new AsyncResult() { override val is =
              Future {
                insertMp3(db, mp3Bytes, imageBytes, genre, title, artist, 0)
              }
            }
          }
        }
      }
    }
  }

  /** 굳이 abusing 들에게 친절한 안내메세지를 보내지와 응답을 보내줄 필요는 없지 않을까?
   *  내 쪽에서만 어떤 에러가 있었는지 추적가능하도록 만들자.
   */
  error {
    case e: SizeConstraintExceededException => halt(500, "Too much!")
    case e: IOException => halt(500, "server denied me")
    case e: NoSuchElementException => halt(500, "nsee")
    case e: NumberFormatException => halt(500, "nf")
    case e: PSQLException => {
      halt(500, "pe")
    }
    case e: Exception => e.printStackTrace()
  }
}

/** 서비스를 제공하는 routing 클래스
 *
 * @param db config 정보
 */
class FileRouteServlet (val db: Database) extends ScalatraServlet with FileRoute {
  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}