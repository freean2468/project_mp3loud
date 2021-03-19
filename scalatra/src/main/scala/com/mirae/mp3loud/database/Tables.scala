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
   * @param title
   * @param artist
   * @param played_times
   * @param origin
   */
  case class Mp3(title: String, artist: String, played_times: Int, origin: Array[Byte])

  /** like_table 의 한 레코드를 모방한 case class 자동 형변환에 사용
   *
   * @param no
   * @param dayOfYear
   * @param answer
   * @param photo
   */
  case class Like(no: String, title: String, artist: String)

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
    def title = column[String]("title")
    def artist = column[String]("artist")
    def playedTimes = column[Int]("played_times")
    def origin = column[Array[Byte]]("origin")

    def pkTitleArtist = primaryKey("pk_title_artist", (title, artist))

    /** Every table needs a * projection with the same type as the table's type parameter */
    def * =
      (title, artist, playedTimes, origin) <> (Mp3.tupled, Mp3.unapply)
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
    def title = column[String]("title")
    def artist = column[String]("artist")

    /** foreign key */
    def user =
      foreignKey("fk_no", no, users)(_.no,
        onUpdate = ForeignKeyAction.Cascade,
        onDelete = ForeignKeyAction.Cascade)

    /** foreign key */
    def mp3 =
      foreignKey("fk_title_artist", (title, artist), mp3s)(mp => (mp.title, mp.artist),
        onUpdate = ForeignKeyAction.Cascade,
        onDelete = ForeignKeyAction.Cascade)

    def * =
      (no, title, artist) <> (Like.tupled, Like.unapply)
  }

  /** db의 like_table과의 쿼리를 담당할 변수
   *
   */
  val likes = TableQuery[Likes]
}