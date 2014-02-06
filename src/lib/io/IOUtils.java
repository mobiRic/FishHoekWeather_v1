package lib.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

public class IOUtils
{
	/**
	 * Copies one stream to another.
	 * 
	 * @param input
	 *            InputStream to copy from
	 * @param output
	 *            OutputStream to copy to
	 * @throws IOException
	 *             if either stream could not be accessed
	 */
	public static void copyStream(InputStream input, OutputStream output) throws IOException
	{
		byte[] buffer = new byte[1024]; // Adjust if you want
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1)
		{
			output.write(buffer, 0, bytesRead);
		}
	}

	/**
	 * Writes a stream to disk. A new file will be written to the application's internal storage
	 * directory with a unique generated name.
	 * 
	 * @param context
	 *            Application context where the file will be written
	 * @param inputStream
	 *            Stream to write
	 * @return name of file created
	 * @throws IOException
	 *             if there is an error writing the file
	 */
	public static String streamToDisk(Context context, InputStream inputStream) throws IOException
	{
		FileOutputStream fos = null;
		try
		{
			File tempFile =
					File.createTempFile("tmp", "png", context.getApplicationContext().getFilesDir());
			fos = new FileOutputStream(tempFile);
			copyStream(inputStream, fos);

			return tempFile.getName();
		}
		finally
		{
			if (fos != null)
			{
				fos.close();
			}

		}
	}

	/**
	 * Writes a stream to disk. The file will be written to the application's internal storage
	 * directory with the provided name. If this file already exists, it will be overwritten.
	 * 
	 * @param context
	 *            Application context where the file will be written
	 * @param inputStream
	 *            Stream to write
	 * @param filename
	 *            name of file to write
	 * @throws IOException
	 *             if there is an error writing the file
	 */
	public static void streamToDisk(Context context, InputStream inputStream, String filename)
			throws IOException
	{
		FileOutputStream fos = null;
		try
		{
			fos = context.getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
			copyStream(inputStream, fos);
		}
		finally
		{
			if (fos != null)
			{
				fos.close();
			}

		}
	}

	/**
	 * Reads a stream from disk. File must be in the application's internal storage directory.
	 * 
	 * @param context
	 *            Application context to search for the file
	 * @param name
	 *            file name
	 * @return {@link FileInputStream} of the provided file name
	 * @throws FileNotFoundException
	 *             if the name is not valid
	 */
	public static FileInputStream diskToStream(Context context, String name)
			throws FileNotFoundException
	{
		return context.getApplicationContext().openFileInput(name);
	}
}
