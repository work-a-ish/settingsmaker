package jp.ne.ibis.qrinfo.settingsmaker.controller

import jp.ne.ibis.qrinfo.settingsmaker.downloads.HTMLBuilder
import jp.ne.ibis.qrinfo.settingsmaker.downloads.JavaScriptBuilder
import jp.ne.ibis.qrinfo.settingsmaker.downloads.PlistBuilder
import jp.ne.ibis.qrinfo.settingsmaker.downloads.YMLBuilder
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.FileOutputStream

/**
 * InputForm画面のコントローラー
 */
@Controller
class InputFormController {

    /**
     * index画面
     */
    @GetMapping("/")
    fun showIndex(model: Model): String {
        return "index"
    }

    /**
     * ファイルD&D用のURL
     */
    @PostMapping("/upload")
    @ResponseBody
    fun uploadIndex(@RequestParam("files") file: MultipartFile, model: Model) {
        // settings.ymlの本文
        val ymlString = YMLBuilder(file.bytes).createYML()
        // index.pugの本文
        val htmlString = HTMLBuilder(file.bytes).createHTML()
        // main.jsの本文
        val jsString = JavaScriptBuilder(file.bytes).createJS()
        // config.plistの本文
        val plistString = PlistBuilder(file.bytes).createPlist()

        // settings.ymlをDLするための一時ファイル
        FileCopyUtils.copy(ymlString.toByteArray(charset("UTF-8")), FileOutputStream("temp_settings.downloads"))
        // index.pugをDLするための一時ファイル
        FileCopyUtils.copy(htmlString.toByteArray(charset("UTF-8")), FileOutputStream("temp_html.downloads"))
        // main.jsをDLするための一時ファイル
        FileCopyUtils.copy(jsString.toByteArray(charset("UTF-8")), FileOutputStream("temp_js.downloads"))
        // config.plistをDLするための一時ファイル
        FileCopyUtils.copy(plistString.toByteArray(charset("UTF-8")), FileOutputStream("temp_plist.downloads"))
    }

    /**
     * settings.ymlのDL用URL
     */
    @GetMapping("/settings.yml", produces = ["application/x-yaml"])
    @ResponseBody
    fun downloadYml(): Resource = FileSystemResource("temp_settings.downloads")

    /**
     * index.pugのDL用URL
     */
    @GetMapping("/index.pug", produces = ["application/octet-stream"])
    @ResponseBody
    fun downloadHtml(): Resource = FileSystemResource("temp_html.downloads")

    /**
     * main.jsのDL用URL
     */
    @GetMapping("/main.js", produces = ["application/octet-stream"])
    @ResponseBody
    fun downloadJS(): Resource = FileSystemResource("temp_js.downloads")

    /**
     * config.plistのDL用URL
     */
    @GetMapping("/config.plist", produces = ["application/octet-stream"])
    @ResponseBody
    fun downloadPlist(): Resource = FileSystemResource("temp_plist.downloads")
}