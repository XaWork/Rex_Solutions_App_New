package com.ajayasija.rexsolutions.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.ajayasija.rexsolutions.domain.model.ImageData

class DBHandler(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LEADID + " TEXT,"
                + VEHNO + " TEXT,"
                + IMAGE_NAME + " TEXT,"
                + IMAGE_PATH + " TEXT,"
                + VIDEO_NAME + " TEXT,"
                + VIDEO_PATH + " TEXT,"
                + UPLOAD_STATUS + " TEXT,"
                + API_STATUS + " TEXT)")
        db.execSQL(query)
    }

    fun addInspection(
        LeadId: String,
        vehNo: String,
        ImageName: String,
        ImagePath: String?,
        upload_status: String?,
        api_status: String?
    ) {
        try {
            val db = this.writableDatabase

            // on below line we are creating a
            // variable for content values.
            val values = ContentValues()

            // on below line we are passing all values
            // along with its key and value pair.
            values.put(LEADID, LeadId)
            values.put(IMAGE_NAME, ImageName)
            values.put(IMAGE_PATH, ImagePath)
            values.put(VEHNO, vehNo)
            values.put(UPLOAD_STATUS, upload_status)
            values.put(API_STATUS, api_status)
            db.insert(TABLE_NAME, null, values)

            // at last we are closing our
            // database after adding database.
            Log.d("LeadId ", "save:$LeadId")
            Log.d("ImageName ", "save:$ImageName")
            db.close()
        } catch (e: Exception) {
            Log.d("errordb", "saving:$e")
        }
    }

    fun addVideoInspection(
        LeadId: String,
        vehNo: String,
        videoName: String,
        videoPath: String?,
        upload_status: String?,
        api_status: String?
    ) {
        try {
            val db = this.writableDatabase

            // on below line we are creating a
            // variable for content values.
            val values = ContentValues()

            // on below line we are passing all values
            // along with its key and value pair.
            values.put(LEADID, LeadId)
            values.put(VIDEO_NAME, videoName)
            values.put(VIDEO_PATH, videoPath)
            values.put(VEHNO, vehNo)
            values.put(UPLOAD_STATUS, upload_status)
            values.put(API_STATUS, api_status)
            db.insert(TABLE_NAME, null, values)

            // at last we are closing our
            // database after adding database.
            Log.d("LeadId ", "save:$LeadId")
            Log.d("VideoName ", "save:$videoName")
            db.close()
        } catch (e: Exception) {
            Log.d("errordb", "saving:$e")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
    val imageUploadDetails: ArrayList<ImageData>
        get() {
            var count = 0
            val alImageData: ArrayList<ImageData> = ArrayList()
            val db = this.readableDatabase

            // on below line we are creating a cursor with query to read data from database.
            val cursorCourses = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + UPLOAD_STATUS + "='N' ",
                null
            )
            //  if(cursorCourses!=null && cursorCourses.getCount()>0) {
            if (cursorCourses.moveToFirst()) {
                do {
                    // on below line we are adding the data from cursor to our array list.
                    val rowId = cursorCourses.getString(0)
                    val LeadId = cursorCourses.getString(1)
                    val vehNo = cursorCourses.getString(2)
                    val ImageName = cursorCourses.getString(3)
                    val ImagePath = cursorCourses.getString(4)
                    //val videoName = cursorCourses.getString(5)
                    //val videoPath = cursorCourses.getString(6)
                    val UploadStatus = cursorCourses.getString(5)
                            Log.d("Saved LeadId", ":$LeadId")
                    Log.d("Saved ImageName", ":$ImageName")
                    Log.d("Saved ImagePath", ":$ImagePath")
                    val imageData = ImageData(rowId, LeadId,vehNo, ImageName, ImagePath, UploadStatus)
                    alImageData.add(imageData)
                    count++
                } while (cursorCourses.moveToNext())
                // moving our cursor to next.
                // }
            }
            // db.close();
            Log.d("Count Images", ":" + alImageData.size)
            return alImageData
        }
    val uploadLeadDetails: Int
        get() {
            var count = 0
            val alImageData: ArrayList<ImageData> = ArrayList<ImageData>()
            val db = this.readableDatabase

            // on below line we are creating a cursor with query to read data from database.
            val cursorCourses = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + UPLOAD_STATUS + "='N' ",
                null
            )
            if (cursorCourses.moveToFirst()) {
                do {
                    // on below line we are adding the data from cursor to our array list.
                    val rowId = cursorCourses.getString(0)
                    val LeadId = cursorCourses.getString(1)
                    val ImageName = cursorCourses.getString(2)
                    val ImagePath = cursorCourses.getString(3)
                    val UploadStatus = cursorCourses.getString(4)
                    Log.d("Saved LeadId", ":$LeadId")
                    Log.d("Saved ImageName", ":$ImageName")
                    Log.d("Saved ImagePath", ":$ImagePath")
                    val imageData = ImageData(rowId, LeadId, ImageName, ImagePath, UploadStatus)
                    count++
                } while (cursorCourses.moveToNext())
                // moving our cursor to next.
            }
            db.close()
            return count
        }

    fun updateUploadStatus(id: Int): Boolean {
        val db = this.readableDatabase
        db.execSQL("UPDATE " + TABLE_NAME + " SET " + UPLOAD_STATUS + "='Y' WHERE " + ID_COL + "=" + id)
        return true
    }

    val leadCount: Int
        get() {
            var count = 0
            val db = this.readableDatabase

            // on below line we are creating a cursor with query to read data from database.
            val cursorCourses = db.rawQuery("SELECT DISTINCT(leadid) FROM " + TABLE_NAME, null)
            if (cursorCourses.count > 0) {
                if (cursorCourses.moveToFirst()) {
                    do {
                        // on below line we are adding the data from cursor to our array list.
                        val LeadId = cursorCourses.getString(0)
                        count++
                    } while (cursorCourses.moveToNext())
                    // moving our cursor to next.
                }
            }
            //  db.close();
            return count
        }
    val imagesCount: Int
        get() {
            var count = 0
            val db = this.readableDatabase

            // on below line we are creating a cursor with query to read data from database.
            val cursorCourses = db.rawQuery("SELECT leadid FROM " + TABLE_NAME, null)
            if (cursorCourses.count > 0) {
                if (cursorCourses.moveToFirst()) {
                    do {
                        // on below line we are adding the data from cursor to our array list.
                        val LeadId = cursorCourses.getString(0)
                        count++
                    } while (cursorCourses.moveToNext())
                    // moving our cursor to next.
                }
            }
            //  db.close();
            return count
        }
    val leadImageUploadDetails: ArrayList<ImageData>
        get() {
            var count = 0
            val alImageData: ArrayList<ImageData> = ArrayList<ImageData>()
            val db = this.readableDatabase

            // on below line we are creating a cursor with query to read data from database.
            val cursorCourses = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + UPLOAD_STATUS + "='Y' ",
                null
            )
            if (cursorCourses.moveToFirst()) {
                do {
                    // on below line we are adding the data from cursor to our array list.
                    val rowId = cursorCourses.getString(0)
                    val LeadId = cursorCourses.getString(1)
                    val ImageName = cursorCourses.getString(2)
                    val ImagePath = cursorCourses.getString(3)
                    val UploadStatus = cursorCourses.getString(4)
                    Log.d("Saved LeadId", ":$LeadId")
                    Log.d("Saved ImageName", ":$ImageName")
                    Log.d("Saved ImagePath", ":$ImagePath")
                    val imageData = ImageData(rowId, LeadId, ImageName, ImagePath, UploadStatus)
                    alImageData.add(imageData)
                    count++
                } while (cursorCourses.moveToNext())
                // moving our cursor to next.
            }
            db.close()
            return alImageData
        }

    fun deleteUploadedImage(LeadId: String, ImageName: String): Int {
        val db = this.readableDatabase
        // Define 'where' part of query.
        val selection = (LEADID + " =?  and "
                + IMAGE_NAME + " =? ")
        // Specify arguments in placeholder order.
        val selectionArgs =
            arrayOf(LeadId, ImageName)
        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    companion object {
        private const val DB_NAME = "rexdb"

        // below int is our database version
        private const val DB_VERSION = 1

        // below variable is for our table name.
        private const val TABLE_NAME = "inspection"

        // below variable is for our id column.
        private const val ID_COL = "id"

        // below variable is for our course name column
        private const val LEADID = "leadid"
        private const val VEHNO = "vehno"

        // below variable id for our course duration column.
        private const val IMAGE_NAME = "image_name"
        private const val IMAGE_PATH = "image_path"

        private const val VIDEO_NAME = "video_name"
        private const val VIDEO_PATH = "video_path"

        private const val UPLOAD_STATUS = "upload_status"
        private const val API_STATUS = "api_status"

        // below variable for our course description column.
        private const val DESCRIPTION_COL = "description"
    }
}