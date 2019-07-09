package jp.ne.ibis.qrinfo.settingsmaker.downloads

import jp.ne.ibis.qrinfo.settingsmaker.utils.ExcelUtils
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream

/**
 * Excelの台帳サンプルを作成するためのクラス
 * @param data 取得した 設定ファイル.xlsx のByteArray
 */
class ExcelBuilder(data: ByteArray) {
    /**
     * 取得した設定ファイルのByteArray
     */
    private val byteData = data
    /**
     * 取得した設定ファイルのシート名
     */
    private val sheetName = "ヘッダー設定"

    /**
     * QRinfo_sample.xlsxを作成する
     */
    fun createExcel() {
        // ヘッダー設定シートのSheetData
        val headerSheetData = ExcelUtils.convertToSheet(byteData, sheetName)
        createWorkbook(headerSheetData)
    }

    /**
     * workbookを作成する
     * @param sheetData ヘッダー設定シートのSheetData
     */
    private fun createWorkbook(sheetData: SheetData) {
        XSSFWorkbook().use { book ->
            // Sheet1作成
            book.createSheet("Sheet1").apply {
                // 1行目作成
                createRow(0).apply {
                    sheetData.forEachIndexed { index, list ->
                        // セルを作成して値をセット
                        createCell(index).setCellValue(list.first())
                    }
                }
                // 2~4行目作成
                for (i in 1..3) {
                    createRow(i).apply {
                        createCell(0).setCellValue("1$i")
                        sheetData.drop(1) // ID列は処理が違うので除外
                                .forEachIndexed { index, list ->
                                    // セルを作成して値をセット
                                    createCell(index + 1).setCellValue("1${i}の${list.first()}")
                                }
                    }
                }
            }

            // 出力
            FileOutputStream("QRinfo_sample.xlsx").use {
                book.write(it)
            }
        }
    }
}