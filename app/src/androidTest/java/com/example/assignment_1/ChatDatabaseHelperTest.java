
package com.example.assignment_1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ChatDatabaseHelperTest {

    private Context context;
    private ChatDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Before
    public void setUp() {

        context = ApplicationProvider.getApplicationContext();

        context.deleteDatabase(ChatDatabaseHelper.DATABASE_NAME);

        dbHelper = new ChatDatabaseHelper(context);

        db = dbHelper.getWritableDatabase();
    }

    @Test
    public void testDatabaseCreation() {

        assertTrue(db.isOpen());

        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{ChatDatabaseHelper.TABLE_NAME}
        );

        assertTrue("Messages table should exist", cursor.moveToFirst());

        cursor.close();
    }

    @Test
    public void testInsertMessage() {

        ContentValues values = new ContentValues();

        values.put(
                ChatDatabaseHelper.KEY_MESSAGE,
                "Hello Android"
        );

        long result = db.insert(
                ChatDatabaseHelper.TABLE_NAME,
                null,
                values
        );

        assertNotEquals(-1, result);
    }

    @Test
    public void testRetrieveMessage() {

        String expectedMessage = "Hello SQLite";

        ContentValues values = new ContentValues();

        values.put(
                ChatDatabaseHelper.KEY_MESSAGE,
                expectedMessage
        );

        db.insert(
                ChatDatabaseHelper.TABLE_NAME,
                null,
                values
        );

        Cursor cursor = db.query(
                ChatDatabaseHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue(cursor.moveToFirst());

        String actualMessage = cursor.getString(
                cursor.getColumnIndexOrThrow(
                        ChatDatabaseHelper.KEY_MESSAGE
                )
        );

        assertEquals(expectedMessage, actualMessage);

        cursor.close();
    }

    @Test
    public void testMultipleMessages() {

        String[] testMessages = {
                "Hello",
                "How are you?",
                "Learning SQLite"
        };

        for (String message : testMessages) {

            ContentValues values = new ContentValues();

            values.put(
                    ChatDatabaseHelper.KEY_MESSAGE,
                    message
            );

            db.insert(
                    ChatDatabaseHelper.TABLE_NAME,
                    null,
                    values
            );
        }

        Cursor cursor = db.query(
                ChatDatabaseHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertEquals(3, cursor.getCount());

        cursor.close();
    }

    @Test
    public void testDatabaseColumnNames() {

        Cursor cursor = db.query(
                ChatDatabaseHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertEquals(2, cursor.getColumnCount());

        assertEquals(
                ChatDatabaseHelper.KEY_ID,
                cursor.getColumnName(0)
        );

        assertEquals(
                ChatDatabaseHelper.KEY_MESSAGE,
                cursor.getColumnName(1)
        );

        cursor.close();
    }

    @After
    public void tearDown() {

        if (db != null && db.isOpen()) {
            db.close();
        }

        if (dbHelper != null) {
            dbHelper.close();
        }

        context.deleteDatabase(ChatDatabaseHelper.DATABASE_NAME);
    }
}
