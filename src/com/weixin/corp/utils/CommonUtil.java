package com.weixin.corp.utils;

import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class CommonUtil {

	public static String getDateStr(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static Date getStrDate(String date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date strDate = null;
		try {
			strDate = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return strDate;
	}

	public static boolean StringisEmpty(String value) {
		if (null == value) {
			return true;
		}
		return "".equals(value.trim());
	}

	/**
	 * ͼƬ���� ѹ��
	 * 
	 * @param outputWidth
	 *            ���ͼƬ��
	 * @param outputHeight
	 *            ���ͼƬ��
	 * @return
	 */
	public static boolean compressPic(File image, int outputHeight,
			int outputWidth) {
		boolean proportion = true;
		try {
			BufferedImage img;
			String type = StringUtils.substringAfterLast(image.getName(), ".");
			if ("jpg".equals(type) || "JPEG".equals(type)) {
				System.out.println("��ͼƬ��׺Ϊ��" + type);
				img = getImage(image);
			} else {
				img = ImageIO.read(image);
			}
			int newWidth;
			int newHeight;
			// �ж��Ƿ��ǵȱ�����
			if (proportion == true) {
				// Ϊ�ȱ����ż��������ͼƬ��ȼ��߶�
				double rate1 = ((double) img.getWidth(null))
						/ (double) outputWidth + 0.1;
				double rate2 = ((double) img.getHeight(null))
						/ (double) outputHeight + 0.1;
				// �������ű��ʴ�Ľ������ſ���
				double rate = rate1 > rate2 ? rate1 : rate2;
				newWidth = (int) (((double) img.getWidth(null)) / rate);
				newHeight = (int) (((double) img.getHeight(null)) / rate);
			} else {
				newWidth = outputWidth; // �����ͼƬ���
				newHeight = outputHeight; // �����ͼƬ�߶�
			}
			BufferedImage tag = new BufferedImage((int) newWidth,
					(int) newHeight, BufferedImage.TYPE_INT_RGB);

			/*
			 * Image.SCALE_SMOOTH �������㷨 ��������ͼƬ��ƽ���ȵ� ���ȼ����ٶȸ� ���ɵ�ͼƬ�����ȽϺ� ���ٶ���
			 */
			tag.getGraphics().drawImage(
					img.getScaledInstance(newWidth, newHeight,
							Image.SCALE_SMOOTH), 0, 0, null);
			FileOutputStream out = new FileOutputStream(image);
			// JPEGImageEncoder������������ͼƬ���͵�ת��
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			encoder.encode(tag);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * jpg ͼƬ�ļ�ģʽת����CMYK����>RGB
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	private static BufferedImage getImage(File file) throws IOException {
		// File file = new File(filename);
		// ����������
		ImageInputStream input = ImageIO.createImageInputStream(file);
		Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
		if (readers == null || !readers.hasNext()) {
			throw new RuntimeException("No ImageReaders found");
		}
		ImageReader reader = (ImageReader) readers.next();
		reader.setInput(input);
		// ��ȡ�ļ���ʽ
		BufferedImage image;
		try {
			// ���Զ�ȡͼƬ (������ɫ��ת��).
			image = reader.read(0); // RGB
			// reader.read(0); // RGB
		} catch (IIOException e) {
			// ��ȡRaster (û����ɫ��ת��). //true��Rgb������Cmyk
			Raster raster = reader.readRaster(0, null);// CMYK
			image = createJPEG4(raster);
		}
		return image;
	}

	// ����Сת��Ϊ KB��M��G
	public static String convertFileSize(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", size);
	}

	// ͼƬ��ʽת�� CMYK����>RGB
	private static BufferedImage createJPEG4(Raster raster) {
		int w = raster.getWidth();
		int h = raster.getHeight();
		byte[] rgb = new byte[w * h * 3];
		// ��ɫ�ռ�ת��
		float[] Y = raster.getSamples(0, 0, w, h, 0, (float[]) null);
		float[] Cb = raster.getSamples(0, 0, w, h, 1, (float[]) null);
		float[] Cr = raster.getSamples(0, 0, w, h, 2, (float[]) null);
		float[] K = raster.getSamples(0, 0, w, h, 3, (float[]) null);
		for (int i = 0, imax = Y.length, base = 0; i < imax; i++, base += 3) {
			float k = 220 - K[i], y = 255 - Y[i], cb = 255 - Cb[i], cr = 255 - Cr[i];

			double val = y + 1.402 * (cr - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
					: (byte) (val + 0.5);

			val = y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base + 1] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
					: (byte) (val + 0.5);

			val = y + 1.772 * (cb - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base + 2] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
					: (byte) (val + 0.5);
		}
		raster = Raster.createInterleavedRaster(new DataBufferByte(rgb,
				rgb.length), w, h, w * 3, 3, new int[] { 0, 1, 2 }, null);
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, false, true,
				Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		return new BufferedImage(cm, (WritableRaster) raster, true, null);
	}
	
	// �ֽ������е�INDEXOF��������STRING���е�INDEXOF����
	public static int byteIndexOf(byte[] b, String s, int start) {
		return byteIndexOf(b, s.getBytes(), start);
	}

	// �ֽ������е�INDEXOF��������STRING���е�INDEXOF����
	public static int byteIndexOf(byte[] b, byte[] s, int start) {
		int i;
		if (s.length == 0) {
			return 0;
		}
		int max = b.length - s.length;
		if (max < 0)
			return -1;
		if (start > max)
			return -1;
		if (start < 0)
			start = 0;
		search: for (i = start; i <= max; i++) {
			if (b[i] == s[0]) {
				int k = 1;
				while (k < s.length) {
					if (b[k + i] != s[k]) {
						continue search;
					}
					k++;
				}
				return i;
			}
		}
		return -1;
	}

	// ���ڴ�һ���ֽ���������ȡһ���ֽ�����
	public static byte[] subBytes(byte[] b, int from, int end) {
		byte[] result = new byte[end - from];
		System.arraycopy(b, from, result, 0, end - from);
		return result;
	}

	// ���ڴ�һ���ֽ���������ȡһ���ַ���
	public static String subBytesString(byte[] b, int from, int end) {
		return new String(subBytes(b, from, end));
	}

	public static void main(String[] args) {
		String dateStr = "2011-01-01 12:01:12";
		System.out.println(getStrDate(dateStr, "yyyy-MM-dd"));
		System.out.println(Calendar.DAY_OF_MONTH);
		System.out.println(getDateStr(DateUtils.round(new Date(), 1),
				"yyyy-MM-dd"));
		System.out.println(getDateStr(DateUtils.round(new Date(), 5),
				"yyyy-MM-dd"));
		// String size = CommonUtil.convertFileSize(1705110020);
		// System.out.println("====================" + size);
		// // =================================================================
		// // �ж��ļ���С
		// // =================================================================
		// String x = StringUtils.substringBefore(size, " ");
		// System.out.println(x);
		// System.out.println(Float.parseFloat(x) > 10);
	}
}
