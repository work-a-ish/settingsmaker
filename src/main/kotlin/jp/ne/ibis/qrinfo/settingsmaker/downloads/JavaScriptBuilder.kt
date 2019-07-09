package jp.ne.ibis.qrinfo.settingsmaker.downloads

import jp.ne.ibis.qrinfo.settingsmaker.utils.ExcelUtils

/**
 * Excelファイルからmain.jsを作成するためのクラス
 * @param data 取得した 設定ファイル.xlsx のByteArray
 */
class JavaScriptBuilder(data: ByteArray) {
    /**
     * 取得した設定ファイルのByteArray
     */
    private val byteData = data
    /**
     * 取得した設定ファイルのシート名のリスト
     */
    private val sheetNames = listOf("基本情報", "参考資料")

    /**
     * main.jsを作成する
     * @return main.jsの本文
     */
    fun createJS(): String {
        // Sheet名とSheetDataのMap
        val settingsSheetData = sheetNames.associate {
            it to ExcelUtils.convertToSheet(byteData, it)
        }

        return createJSString(settingsSheetData)
    }

    /**
     * main.jsの本文を作成する
     * @param sheetDataMap 各シート名とSheetDataのMap
     * @return main.jsの本文
     */
    private fun createJSString(sheetDataMap: Map<String, SheetData>): String {

        // querySelector部分
        val querySelector = sheetDataMap.getValue("基本情報")
                .drop(1)
                .joinToString("\n") {
                    if (it.size == 3) { // 3列目（mailto:やtel:）があったときはリンクとして処理
                        """
                            |    qs('#app .equipment .${it[0]} .value').textContent = jsonData.${it[1]}
                            |    qs('#app .equipment .${it[0]} .value').setAttribute('href', '${it[2]}' + jsonData.${it[1]})
                        """.trimMargin()
                    } else { // それ以外は文字列として処理
                        "    qs('#app .equipment .${it[0]} .value').textContent = jsonData.${it[1]}"
                    }
                }

        // manuals部分
        val manuals = sheetDataMap.getValue("参考資料")
                .joinToString("\n") {
            "    pushToManualsIfNeeded(jsonData.${it[0]}, jsonData.${it[1]})"
        }

        return """
            |/**
            | * jsonを引数にとって画面に描画する関数
            | * @param {*} json apiのresultプロパティのオブジェクト
            | */
            |window.render = (json) => {
            |    
            |    // パース  
            |    const data = JSON.parse(json)
            |    const jsonData = data.json_data
            |    
            |    // document.querySelectorはなんども使うので短くする
            |    const qs = selector => document.querySelector(selector)
            |    // 値を代入していく
            |    qs('#app .equipment .id .value').textContent = data.id
            |$querySelector
            |
            |
            |    // 値があればmanualsの中に入れる。 
            |    const manuals = []
            |    
            |    // リンクタイトルとURLがどちらかある際manualsにpushする関数。
            |    const pushToManualsIfNeeded = (linkName, linkURL) => {
            |        if (linkName || linkURL) {
            |            manuals.push({name: linkName, link: linkURL})
            |        }
            |    }
            |$manuals
            |
            |    if (manuals.length == 0){ // マニュアルがなかったらそのまま終了
            |        return
            |    }
            |    const dom_manuals = qs('#app .manuals .list')
            |
            |    // 要素から全ての要素を削除
            |    while (dom_manuals.firstChild)
            |        dom_manuals.removeChild(dom_manuals.firstChild)
            |
            |    // マニュアル名とリンクを引数にDOMを生成する関数
            |    const createManualDom = (manualname, manuallink) => {
            |
            |        // マニュアル名dom
            |        let name = document.createElement('div')
            |        name.classList.add('key')
            |        name.textContent = manualname
            |    
            |        // マニュアルリンクdom
            |        let link = document.createElement('a')
            |        link.setAttribute('href', manuallink)
            |        link.textContent = manuallink
            |    
            |        // 値dom
            |        let value = document.createElement('div')
            |        value.classList.add('value')
            |        
            |    
            |        // 親要素dom
            |        let card = document.createElement('li')
            |        card.classList.add('card')
            |        
            |        // 組み立て
            |        value.appendChild(link)
            |        card.appendChild(name)
            |        card.appendChild(value)
            |    
            |        return card
            |    }
            |
            |    // マニュアルの数だけappend
            |    for (manual of manuals)
            |        dom_manuals.appendChild(createManualDom(manual.name, manual.link))
            |}
        """.trimMargin()
    }
}