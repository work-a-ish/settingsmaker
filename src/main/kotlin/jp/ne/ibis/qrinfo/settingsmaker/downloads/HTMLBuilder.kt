package jp.ne.ibis.qrinfo.settingsmaker.downloads

import jp.ne.ibis.qrinfo.settingsmaker.utils.ExcelUtils

/**
 * Excelファイルからindex.pugを作成するためのクラス
 * @param data 取得した 設定ファイル.xlsx のByteArray
 */
class HTMLBuilder(private val data: ByteArray) {
    /**
     * 取得した設定ファイルのシート名
     */
    private val sheetName = "基本情報"

    /**
     * index.pugを作成する
     * @return index.pugの本文
     */
    fun createHTML(): String {
        val htmlSheetData = ExcelUtils.convertToSheet(data, sheetName)
        return createHTMLString(htmlSheetData)
    }

    /**
     * index.pugの本文を作成する
     * @param sheetData 基本情報シートのsheetData
     * @return index.pugの本文
     */
    private fun createHTMLString(sheetData: SheetData): String {
        // ul.listの内部部分
        val cardPart = sheetData.joinToString("\n") {
            """
                |                    li.card.${it[0]}
                |                        div.key ${it[1]}
                |                        a.value
            """.trimMargin()
        }

        return """            
            |<!DOCTYPE html>
            |html(lang="ja")
            |    head
            |        meta(charset="UTF-8")
            |        meta(name="viewport", content="width=device-width, initial-scale=1.0")
            |        meta(http-equiv="X-UA-Compatible", content="ie=edge")
            |        meta(name="format-detection", content="telephone=no")
            |        link(rel="stylesheet", href="style.css")
            |        title bqr result
            |    body
            |        div#mount
            |        div#app
            |            div.section.equipment
            |                div.title 基本情報
            |                ul.list
            |$cardPart
            |            div.section.manuals
            |                div.title 参考資料
            |                ul.list
            |                    li.card.none
            |                      div 参考資料はありません  
            |        script(src="main.js")
        """.trimMargin()
    }
}