package com.mirae.mp3loud.database

import com.mirae.mp3loud.database.Tables.{Mp3, Mp3s, User, mp3s, users}
import org.scalatra.{ActionResult, NotFound, Ok}
import org.slf4j.LoggerFactory
import slick.dbio.DBIO
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

  /** Read
   *
   */
  def selectUserAll(db: Database) =
    db.run(users.result)

  def selectMp3All(db: Database) =
    db.run(mp3s.result)

  def findUser(db: Database, no: String) =
    db.run(users.filter(_.no === no).result.headOption)

  def findMp3(db: Database, id: Int) =
    db.run(mp3s.filter(_.id === id).result.headOption)

//  def findLikes(db: Database, no: String) =
//    db.run(diaries.filter(d =>
//      d.no === no && (d.dayOfYear >= firstDay && d.dayOfYear <= lastDay)).result)

  /**
   * 유저가 로그인 시 호출하는 함수. 회원 번호를 받아 user_table에 계정이 없으면 새로 생성한다.
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
                logger.debug(s"no : ${no}")
                prom.complete(Try(Ok(count)))
              }
            }
          }
          case Some(x) => {
            selectMp3All(db) onComplete {
              case Success(r) => {
                prom.complete(Try(Ok(r)))
              }
              case Failure(e) => {
                prom.failure(e)
                e.printStackTrace()
              }
            }
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

  /** Delete
   *
   */
//  def delete(db: Database): Unit = {
//    val deleteAction = (accounts filter { _.no like "%test%" }).delete
//    db.run(deleteAction)
//  }
}
