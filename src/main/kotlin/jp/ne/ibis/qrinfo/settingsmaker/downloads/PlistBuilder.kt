package jp.ne.ibis.qrinfo.settingsmaker.downloads

import jp.ne.ibis.qrinfo.settingsmaker.utils.ExcelUtils

/**
 * Excelファイルからplistを作成するためのクラス
 * @param data 取得した 設定ファイル.xlsx のByteArray
 */
class PlistBuilder(private val data: ByteArray) {
    /**
     * 取得した設定ファイルのシート名
     */
    private val sheetName = "plist情報"

    /**
     * config.plistを作成する
     * @return config.plistの本文
     */
    fun createPlist(): String {
        val plistSheetData = ExcelUtils.convertToSheet(data, sheetName)
        return createPlistString(plistSheetData)
    }

    /**
     * config.plistの本文を作成する
     * @param sheetData 参考資料シートのsheetData
     * @return config.plistの本文
     */
    private fun createPlistString(sheetData: SheetData): String {

        return """
            |<?xml version="1.0" encoding="UTF-8"?>
            |<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
            |<plist version="1.0">
            |<dict>
            |    <key>strings</key>
            |    <dict>
            |        <key>qrInformationViewTitle</key>
            |        <string>${sheetData[0][1]}</string>
            |        <key>detectDialogTitle</key>
            |        <string>${sheetData[1][1]}</string>
            |        <key>detectDialogBody</key>
            |        <string>${sheetData[2][1]}:%@
            |詳細を表示しますか？</string>
            |    </dict>
            |    <key>scheme</key>
            |    <string>${sheetData[3][1]}</string>
            |    <key>authToken</key>
            |    <string>${sheetData[4][1]}</string>
            |    <key>apiPath</key>
            |    <string>${sheetData[5][1]}</string>
            |</dict>
            |</plist>
        """.trimMargin()
    }
}