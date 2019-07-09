package jp.ne.ibis.qrinfo.settingsmaker.downloads

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import jp.ne.ibis.qrinfo.settingsmaker.utils.ExcelUtils
import java.io.File
import javax.imageio.ImageIO
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.io.FileInputStream


/**
 * 台帳ファイルから取得したQRコードを生成するためのクラス
 */
class QRCodeGenerator {
    /**
     * QRコードの文頭につける文字列
     */
    private val qrHeaderString = "bqr-komatsu://equipment?id="

    fun generateQR(data: ByteArray) {
        // 台帳のSheet1から取得したSheetData
        val ids = ExcelUtils.convertToSheet(data, "Sheet1")
                .drop(1)
                .map { it.first() }
        createQR(ids)
        zip(ids)
    }

    /**
     * QRコードファイルを作成する
     * @param ids IDのリスト
     */
    private fun createQR(ids: List<String>) {
        ids.forEach {
            // QRコードを生成する文字列
            val source = qrHeaderString + it
            //QRコード生成時のエンコーディング
            val encoding = "UTF-8"
            //サイズ(ピクセル)
            val size = 100
            //画像ファイルの保存先
            val filePath = "$it.png"
            //生成処理
            val hints = mapOf(
                    //エラー訂正レベル指定
                    EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
                    //エンコーディング指定
                    EncodeHintType.CHARACTER_SET to encoding,
                    //マージン指定
                    EncodeHintType.MARGIN to 0
            )
            val bitMatrix = QRCodeWriter().encode(source, BarcodeFormat.QR_CODE, size, size, hints)
            val image = MatrixToImageWriter.toBufferedImage(bitMatrix)

            //ファイルへの保存処理
            ImageIO.write(image, "png", File(filePath))
        }
    }

    /**
     * まとめてDLするためにZipする
     * @param ids IDのリスト
     */
    private fun zip(ids: List<String>) {
        // zipファイル作成
        val buf = ByteArray(1024)
        ZipOutputStream(FileOutputStream(File("QRCode.zip"))).use { zipOutputStream ->
            ids.forEach {
                zipOutputStream.putNextEntry(ZipEntry("$it.png"))
                val fileInputStream = FileInputStream("$it.png")
                val len = fileInputStream.read(buf)
                zipOutputStream.write(buf, 0, len)
            }
        }
        // いらなくなった一時ファイルを削除
        ids.forEach {
            File("$it.png").delete()
        }
    }
}