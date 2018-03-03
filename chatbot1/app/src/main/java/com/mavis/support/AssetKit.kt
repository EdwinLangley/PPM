package com.mavis.support

import android.content.Context
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by darylcecile on 22/02/2018.
 */

class AssetKit(context: Context) {

    private var ctx : Context = context

    public fun copyAllAssets(){

        for (folder in ctx.assets.list("bots/super")){

            for (file in ctx.assets.list("bots/super/" + folder)){

                copyFile(file,"bots/super/" + folder)

            }

        }

    }

    public fun getPath():String{
        return ctx.applicationInfo.dataDir
    }

    public fun copyFile(assetName:String,folderName:String){

        val dataDir = getPath()

        var ins = ctx.assets.open( folderName + "/" + assetName )
        val out = FileOutputStream(dataDir + ( if (dataDir.endsWith('/')) "" else "/" ) + folderName + "/" + assetName )

        try{
            copyFile(ins,out)
        } catch (IOE : IOException){
            error("ERROR")
        }

        ins.close()
        ins = null
        out.flush()
        out.close()

    }

    @Throws(IOException::class)
    public fun copyFile(ins: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int = ins.read(buffer)
        while (read != -1) {
            out.write(buffer, 0, read)
            read = ins.read(buffer)
        }
    }

}