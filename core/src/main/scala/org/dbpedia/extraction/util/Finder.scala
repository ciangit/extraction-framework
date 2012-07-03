package org.dbpedia.extraction.util

import java.util.regex.Pattern
import Finder._

private object Finder {
  val datePattern = Pattern.compile("\\d{8}")
  def dateFilter(name: String) = datePattern.matcher(name).matches 
}

/**
 * Helps to find files and directories in a directory structure as used by the Wikipedia
 * dump download site, for example baseDir/enwiki/20120403/enwiki-20120403-pages-articles.xml.bz2
 */
class Finder[T <% FileLike[T]](baseDir: T, language: Language) {
  
  def this(baseDir: T, language: String) = this(baseDir, Language(language))
  
  /**
   * directory name/file prefix for language, e.g. "en" -> "enwiki"
   */
  val wikiName = language.filePrefix+"wiki"

  /**
   * Directory for language, e.g. "baseDir/enwiki"
   */
  val wikiDir = baseDir.resolve(wikiName)
  
  /**
   * date directory for language, e.g. "20120403" -> "baseDir/enwiki/20120403"
   */
  def directory(date: String) = wikiDir.resolve(date)
  
  /**
   * Finds the names (which are dates in format YYYYMMDD) of dump directories for the language.
   * @tagFile Return only directories that contain a file with this suffix, 
   * e.g. "download-complete" -> "baseDir/enwiki/20120403/enwiki-20120403-download-complete". May be null.
   * @return dates in ascending order
   */
  def dates(suffix: String = null) : List[String] = {
    val suffixFilter = if (suffix == null) {date: String => true} else {date: String => file(date, suffix).exists}
    wikiDir.list.filter(dateFilter).filter(suffixFilter).sortBy(_.toInt)
  }
    
  /**
   * File with given name suffix in main directory for language, e.g. "download-running" ->
   * "baseDir/enwiki/enwiki-download-running"
   */
  def file(suffix: String) = wikiDir.resolve(wikiName+'-'+suffix)
  
  /**
   * File with given name suffix in latest directory for language, e.g. "pages-articles.xml" ->
   * "baseDir/enwiki/20120403/enwiki-20120403-pages-articles.xml"
   */
  def file(date: String, suffix: String) = directory(date).resolve(wikiName+'-'+date+'-'+suffix)
}