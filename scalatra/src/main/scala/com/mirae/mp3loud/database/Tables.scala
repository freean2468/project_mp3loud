package com.mirae.mp3loud.database

import slick.jdbc.PostgresProfile.api._

/** Slick에서 제공하는 Functional Relational Mapping(FRM)을 담은 object
 * 이 안에서 서비스에 필요한 테이블 쿼리를 정의한다.
 *
 */
object Tables {

  /** user_table의 한 레코드를 모방한 case class 자동 형변환에 사용
   *
   * @param no 유저의 카카오톡 회원번호
   */
  case class User(no: String)

  /** mp3_table 의 한 레코드를 모방한 case class 자동 형변환에 사용
   *
   * @param id
   * @param played_times
   * @param title
   * @param artist
   * @param playLengthInSec
   * @param sample
   * @param origin
   */
  case class Mp3(id: Int, played_times: Int, title: String, artist: String, playLengthInSec: Int,
                 sample: Array[Byte], origin: Array[Byte])

  /** like_table 의 한 레코드를 모방한 case class 자동 형변환에 사용
   *
   * @param no
   * @param dayOfYear
   * @param answer
   * @param photo
   */
  case class Like(no: String, id: Int)

  /** db의 user_table을 모방한 클래스
   *
   * @param tag 테이블 이름
   */
  class Users(tag: Tag) extends Table[User](tag, "user_table") {
    /** Columns */
    def no = column[String]("no", O.PrimaryKey)

    /** Every table needs a * projection with the same type as the table's type parameter */
    def * = no <> (User.apply _, User.unapply)
  }

  /** user_table과의 쿼리를 담당할 변수
   *
   */
  val users = TableQuery[Users]

  /** db의 mp3_table을 모방한 클래스
   *
   * @param tag 테이블 이름
   */
  class Mp3s(tag: Tag) extends Table[Mp3](tag, "mp3_table") {
    /** Columns */
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def playedTimes = column[Int]("played_times")
    def title = column[String]("title")
    def artist = column[String]("artist")
    def playLengthInSec = column[Int]("play_length_in_sec")
    def sample = column[Array[Byte]]("sample")
    def origin = column[Array[Byte]]("origin")

    /** Every table needs a * projection with the same type as the table's type parameter */
    def * =
      (id, playedTimes, title, artist, playLengthInSec, sample, origin) <> (Mp3.tupled, Mp3.unapply)
  }

  /** mp3_table과의 쿼리를 담당할 변수
   *
   */
  val mp3s = TableQuery[Mp3s]

  /** db의 like_table을 모방한 클래스
   *
   * @param tag 테이블 이름
   */
  class Likes(tag: Tag) extends Table[Like](tag, "like_table") {
    /** Columns */
    def no = column[String]("no")
    def id = column[Int]("id")

    /** foreign key */
    def user =
      foreignKey("fk_no", no, users)(_.no,
        onUpdate = ForeignKeyAction.Cascade,
        onDelete = ForeignKeyAction.Cascade)

    /** foreign key */
    def mp3 =
      foreignKey("fk_id", id, mp3s)(_.id,
        onUpdate = ForeignKeyAction.Cascade,
        onDelete = ForeignKeyAction.Cascade)

    def * =
      (no, id) <> (Like.tupled, Like.unapply)
  }

  /** db의 like_table과의 쿼리를 담당할 변수
   *
   */
  val likes = TableQuery[Likes]
}