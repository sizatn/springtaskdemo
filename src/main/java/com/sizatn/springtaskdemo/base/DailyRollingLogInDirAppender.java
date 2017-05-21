package com.sizatn.springtaskdemo.base;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

public final class DailyRollingLogInDirAppender extends DailyRollingFileAppender {

	/**
	 * 默认文件大小 10MB.
	 */
	private long fileSize = 10 * 1024 * 1024;
	private String maxFileSize = "10MB";

	/**
	 * 备份文件个数默认为10个
	 */
	private int maxBackupIndex = 10;

	private SimpleDateFormat sdf;
	private String logDirName;
	private String preLogDirName;
	private String logDir;
	private String logFileName;

	/**
	 * 实现父类无参构造方法
	 */
	public DailyRollingLogInDirAppender() {
		super();
	}

	/**
	 * 实现父类有参构造方法
	 */
	public DailyRollingLogInDirAppender(Layout layout, String filename, String datePattern) throws IOException {
		super(layout, filename, datePattern);
	}

	public void activateOptions() {
		super.activateOptions();
		File file = new File(fileName);
		logDir = file.getParent();
		logFileName = file.getName();
		sdf = new SimpleDateFormat(this.getDatePattern());
		logDirName = sdf.format(new Date());
		preLogDirName = sdf.format(new Date(file.lastModified()));
	}

	public void doRollOver() {
		File target;
		File file;
		LogLog.debug("rolling over count=" + ((CountingQuietWriter) qw).getCount());
		LogLog.debug("maxBackupIndex=" + maxBackupIndex);

		createDir(logDirName);

		String cycleFileName = logDir + File.separator + logDirName + File.separator + logFileName;

		if (maxBackupIndex > 0) {// 需要备件文件
			// 删除多出的文件，使文件个数不会大于备份文件数，保持最新的文件
			file = new File(cycleFileName + '.' + maxBackupIndex);
			if (file.exists()) {
				file.delete();
			}

			// 移动文件名{(maxBackupIndex - 1), ..., 2, 1} to {maxBackupIndex, ...,3,2}
			for (int i = maxBackupIndex - 1; i >= 1; i--) {
				file = new File(cycleFileName + "." + i);
				if (file.exists()) {
					target = new File(cycleFileName + '.' + (i + 1));
					LogLog.debug("Renaming file " + file + " to " + target);
					file.renameTo(target);
				}
			}

			// Rename fileName to fileName.1
			target = new File(cycleFileName + "." + 1);

			this.closeFile(); // keep windows happy.

			file = new File(fileName);
			LogLog.debug("Renaming file " + file + " to " + target);
			file.renameTo(target);
		}

		try {
			this.setFile(fileName, false, bufferedIO, bufferSize);
		} catch (IOException e) {
			LogLog.error("setFile(" + fileName + ", false) call failed.", e);
		}
	}

	private void createDir(String dirName) {
		File file = new File(logDir, dirName);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	private void checkFile() {
		try {
			logDirName = sdf.format(new Date());
			if (!logDirName.equals(preLogDirName)) {
				String cycleFileName = logDir + File.separator + logFileName + preLogDirName;
				File file = new File(cycleFileName);
				if (file.exists()) {
					createDir(preLogDirName);
					cycleFileName = logDir + File.separator + preLogDirName + File.separator + logFileName;
					File temp = new File(cycleFileName);
					if (temp.exists()) {
						temp.delete();
					}
					file.renameTo(temp);
				}
				preLogDirName = logDirName;
			}
		} catch (Exception e) {
			LogLog.error("out error " + e.getMessage());
		}
	}

	public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize) throws IOException {
		super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
		if (append) {
			File f = new File(fileName);
			((CountingQuietWriter) qw).setCount(f.length());
		}
	}

	protected void subAppend(LoggingEvent event) {
		super.subAppend(event);
		checkFile();
		if ((fileName != null) && ((CountingQuietWriter) qw).getCount() >= fileSize) {
			this.doRollOver();
		}
	}

	protected void setQWForFiles(Writer writer) {
		this.qw = new CountingQuietWriter(writer, errorHandler);
	}

	public String getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(String value) {
		fileSize = OptionConverter.toFileSize(value, fileSize + 1);
	}

	public int getMaxBackupIndex() {
		return maxBackupIndex;
	}

	public void setMaxBackupIndex(int maxBackupIndex) {
		this.maxBackupIndex = maxBackupIndex;
	}

}
