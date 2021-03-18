package com.mirae.mp3loud.helper

import org.slf4j.LoggerFactory

import java.nio.charset.StandardCharsets
import java.time.temporal.TemporalAdjusters
import java.time.{LocalDate, Year}
import java.util.{Calendar, Date}

/** 각종 기능의 함수들을 모아놓은 object
 *
 */
object Util {

  def convertBytesArrayToString(bytesArray: Array[Byte]) = {
    var s: String = null
    if (bytesArray.length > 0) {
      val logger = LoggerFactory.getLogger(getClass)
      logger.info("byte Array photo length : " + bytesArray)
      s = new String(bytesArray, StandardCharsets.UTF_8)
      logger.info("sPhoto length : " + s.length)
    }
    s
  }
}
