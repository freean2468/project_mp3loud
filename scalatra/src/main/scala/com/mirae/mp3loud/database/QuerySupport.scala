package com.mirae.mp3loud.database

import com.mirae.mp3loud.database.Tables.{Like, LikeConverted, Mp3, Mp3Converted, Mp3s, User, likes, mp3s, users}
import com.mirae.mp3loud.helper.Util
import org.scalatra.servlet.FileItem
import org.scalatra.{ActionResult, NotFound, Ok}
import org.slf4j.LoggerFactory
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Promise
import scala.util.{Failure, Success, Try}


/** Tables의 모방 테이블을 활용한 쿼리문 지원하는 trait, Route class에 mixing in!
 *
 * @param db 첫 servlet 생성 시 전달된 db config
 */
trait QuerySupport {
  import scala.concurrent.ExecutionContext.Implicits.global

  /** Create
   *
   */
  def insert(db: Database, user: User) = db.run(users += user)
  def insert(db: Database, mp3: Mp3) = db.run(mp3s += mp3)
  def insert(db: Database, like: Like) = db.run(likes += like)

  def insertMp3(db: Database, mp3: FileItem, image: FileItem, genre: String, title: String, artist: String, playLengthInSec: Int) = {
    val prom = Promise[ActionResult]()
    insert(db, Mp3(genre, title, artist, 0, mp3.get(), image.get())) onComplete {
      case Failure(e) => prom.failure(e)
      case Success(s) => prom.complete(Try(Ok(s)))
    }
    prom.future
  }

  def insertLike(db: Database, no: String, title: String, artist: String) = {
    val prom = Promise[ActionResult]()
    insert(db, Like(no, title, artist)) onComplete {
      case Failure(e) => prom.failure(e)
      case Success(s) => prom.complete(Try(Ok(s)))
    }
    prom.future
  }

  /** Read
   *
   */
  def selectUserAll(db: Database) =
    db.run(users.result)

  def selectMp3All(db: Database) =
    db.run(mp3s.result)

  def findUser(db: Database, no: String) =
    db.run(users.filter(_.no === no).result.headOption)

  def findMp3(db: Database, title: String, artist: String) =
    db.run(mp3s.filter(_.title === title).filter(_.artist === artist).result.headOption)

  def findLikes(db: Database, no: String) =
    db.run(likes.filter(d => d.no === no).result)

  def retrieveMp3(db: Database, title: String, artist: String) = {
    val prom = Promise[ActionResult]()

    findMp3(db, title, artist) onComplete {
      case Failure(e) => {
        prom.failure(e)
        e.printStackTrace()
      }
      case Success(mp3) => {
        mp3 match {
          case Some(m) => prom.complete(Try(Ok({ "mp3" -> Util.convertBytesArrayToBase64String(m.mp3) })))
          case None => prom.complete(Try(NotFound("file not found")))
        }
      }
    }
    prom.future
  }

  def retrieveMp3List(db: Database) = {
    val logger = LoggerFactory.getLogger(getClass)
    val prom = Promise[ActionResult]()
    selectMp3All(db) onComplete {
      case Failure(e) => {
        prom.failure(e)
        e.printStackTrace()
      }
      case Success(mp3) => {
//        logger.info(s"mp3.length : ${mp3(0).mp3.length}")
        val convertedArray =
          for (m <- mp3)
            yield(Mp3Converted(m.genre, m.title, m.artist, m.playedTimes, Util.convertBytesArrayToBase64String(m.image)))
//        convertedArray map (elem => logger.info(s"image.length : ${elem.image.length}"))

        prom.complete(Try(Ok(convertedArray)))
      }
    }
    prom.future
  }

  def retrieveLikeList(db: Database, no: String) = {
    val prom = Promise[ActionResult]()
    findLikes(db, no) onComplete {
      case Failure(e) => {
        prom.failure(e)
        e.printStackTrace()
      }
      case Success(like) => {
        val convertedArray =
          for (v <- like)
            yield(LikeConverted(v.title, v.artist))
        prom.complete(Try(Ok(convertedArray)))
      }
    }
    prom.future
  }

  /** 유저가 로그인 시 호출하는 함수. 회원 번호를 받아 user_table에 계정이 없으면 새로 생성한다.
   *
   * @param db
   * @param no 유저 카카오톡 회원 번호
   * @return 비동기 diary record
   */
  def login(db: Database, no:String) = {
    val logger = LoggerFactory.getLogger(getClass)

    val prom = Promise[ActionResult]()
    findUser(db, no) onComplete {
      case Failure(e) => {
        prom.failure(e)
        e.printStackTrace()
      }
      case Success(count) => {
        count match {
          case None => {
            insert(db, User(no)) onComplete {
              case Failure(e) => {
                prom.failure(e)
                e.printStackTrace()
              }
              case Success(count) => {
//                logger.debug(s"no : ${no}")
                prom.complete(Try(Ok("res" -> 0)))
              }
            }
          }
          case Some(x) => {
            prom.complete(Try(Ok("res" -> 1)))
          }
        }
      }
    }
    prom.future
  }

  /** Update
   *
   */
//  def updateAccountPassword(db: Database, newA:Account) = {
//    val updateAction = (for {a <- accounts if a.no === newA.no} yield a.pw).update(newA.pw.orNull)
//    db.run(updateAction)
//  }
//
//  def updateDiary(db: Database, newD: Diary) = {
//    val updateAction = (for {
//      d <- diaries if d.no === newD.no && d.dayOfYear === newD.dayOfYear
//    } yield (d.answer, d.photo)).update((newD.answer.orNull, newD.photo.get))
//    db.run(updateAction)
//  }

  def increasePlayedTimes(db: Database, title: String, artist: String) = {
    val prom = Promise[ActionResult]()
    findMp3(db, title, artist) onComplete {
      case Failure(e) => {
        prom.failure(e)
        e.printStackTrace()
      }
      case Success(s) => {
        db.run((for {mp3 <- mp3s if mp3.title === title && mp3.artist === artist} yield mp3.playedTimes).update(s.get.playedTimes+1)) onComplete {
          case Failure(e) => {
            prom.failure(e)
            e.printStackTrace()
          }
          case Success(count) => {
            prom.complete(Try(Ok(count)))
          }
        }
      }
    }

    prom.future
  }

  /** Delete
   *
   */
  def delete(db: Database, like:Like): Unit = {
    db.run((likes filter { l => l.title === l.artist && l.artist === like.artist }).delete)
  }
}
