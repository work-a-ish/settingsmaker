package jp.ne.ibis.qrinfo.settingsmaker.downloads

import jp.ne.ibis.qrinfo.settingsmaker.utils.ExcelUtils.convertToSheet

typealias CellString = String
typealias RowData = List<CellString>
typealias SheetData = List<RowData>

/**
 * Excelファイルからymlを作成するためのクラス
 * @param data 取得した 設定ファイル.xlsx のByteArray
 */
class YMLBuilder(data: ByteArray) {
    /**
     * 取得した設定ファイルのByteArray
     */
    private val byteData = data
    /**
     * 取得した設定ファイルのシート名のリスト
     */
    private val sheetNames = listOf("ヘッダー設定", "文字数設定", "DB設定", "Box情報", "メール情報", "メール送信先")

    /**
     * settings.ymlを作成する
     * @return settings.ymlの本文
     */
    fun createYML(): String {
        // Sheet名とSheetDataのMap
        val settingsSheetData = sheetNames.associate {
            it to convertToSheet(byteData, it)
        }

        return createYmlString(settingsSheetData)
    }

    /**
     * settings.ymlの本文を作成する
     * @param sheetDataMap 各シート名とSheetDataのMap
     * @return settings.ymlの本文
     */
    private fun createYmlString(sheetDataMap: Map<String, SheetData>): String {

        // excel_excepted_headers部分
        val headerPart = "excel_excepted_headers:\n" +
                sheetDataMap.getValue("ヘッダー設定")
                        .flatten()
                        .joinToString("\n") { "  - $it" }

        // XX_length_limit部分
        val lengthPartKeys = listOf("id_length_limit:", "cell_length_limit:")
        val lengthPart = sheetDataMap
                .getValue("文字数設定")
                .mapIndexed { index, row ->
                    "${lengthPartKeys[index]} ${row[1]}"
                }
                .joinToString("\n")

        // pg_XX部分
        val postgresPartKeys = listOf("pg_host:", "pg_port:", "pg_user:",
                "pg_password:", "pg_database:", "pg_table:")
        val postgresPart = sheetDataMap
                .getValue("DB設定")
                .mapIndexed { index, row ->
                    "${postgresPartKeys[index]} ${row[1]}"
                }
                .joinToString("\n")

        // box_XX部分
        val boxPart = "box_target_fileids:\n" +
                sheetDataMap.getValue("Box情報")
                        .joinToString("\n") {
                            """
                            |  - fileName: ${it[0]}
                            |    fileId: ${it[1]}
                            """.trimMargin()
                        }

        // smtp_XX部分
        val mailPartKeys = listOf("smtp_host:", "smtp_port:", "smtp_user:",
                "smtp_password:", "mail_from:", "mail_subject:")
        val mailPart = sheetDataMap.getValue("メール情報")
                .mapIndexed { index, row ->
                    "${mailPartKeys[index]} ${row[1]}"
                }
                .joinToString("\n")

        // mail_to_list部分
        val mailToList = "mail_to_list:\n" +
                sheetDataMap.getValue("メール送信先")
                        .flatten()
                        .joinToString("\n") { "  - $it" }

        return """
            |# Excel関連
            |$headerPart
            |$lengthPart
            |
            |# postgres関連
            |$postgresPart
            |
            |# box関連
            |box_config_name: staging_config.json
            |$boxPart
            |
            |# メール関連
            |mime_charset: UTF-8
            |$mailPart
            |$mailToList
            """.trimMargin()
    }

}