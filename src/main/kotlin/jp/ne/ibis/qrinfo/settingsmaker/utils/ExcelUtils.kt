package jp.ne.ibis.qrinfo.settingsmaker.utils

import jp.ne.ibis.qrinfo.settingsmaker.downloads.SheetData
import org.apache.poi.ss.usermodel.*
import java.io.ByteArrayInputStream
import java.util.*

object ExcelUtils {
    /**
     * InputStreamからSheetを取得してSheetDataに変換して返す
     * @param sheetName 取得するシートのシート名
     * @return SheetData
     */
    fun convertToSheet(byteData: ByteArray, sheetName: String): SheetData {
        ByteArrayInputStream(byteData).use { stream ->
            val workbook = WorkbookFactory.create(stream)
            val sheet = checkNotNull(workbook.getSheet(sheetName))
            return convertSheetToData(sheet, workbook)
        }
    }

    /**
     * SheetをSheetDataに変換する
     * @param sheet apache.poiのSheet
     * @param book apache.poiのWorkbook
     * @return SheetData
     */
    private fun convertSheetToData(sheet: Sheet, book: Workbook): SheetData {
        val dataFormatter = DataFormatter(Locale.JAPANESE)
        val formulaEvaluator = book.creationHelper.createFormulaEvaluator()
        // シートから二次元配列に変換する。
        return sheet.map { row ->
            // SheetをRowの配列として走査する。
            row.map { cell ->
                when (cell.cellType) {
                    Cell.CELL_TYPE_STRING -> cell.stringCellValue
                    Cell.CELL_TYPE_NUMERIC -> dataFormatter.formatCellValue(cell)
                    Cell.CELL_TYPE_FORMULA -> dataFormatter.formatCellValue(formulaEvaluator.evaluateInCell(cell))
                    else -> ""
                }
            }
        }
    }

}