package kms2.EagleEye;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class AccessSettingFile {
	private static final String FILE_CODE = "UTF-8";
	private static final String DLM = "<>";
	private static final String BUFFER_DATA = "0"; 
	public static final int SETTING_DATA_NUM = 7;
	public static final int SETTING_BUFFER_NUM = 3;
    
	/**
	 * �ݒ�t�@�C���쐬���\�b�h
	 * 
	 * �ݒ�t�@�C�����쐬�A�X�V����
	 * ����Ftrue
	 * �ُ�:false
	 * 
	 * @param String, String
	 * @return�@boolean
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static boolean makeSettingFile(String filePath, String fileName, String gmail, int display, int full, int alarm){
		File file = new File(filePath + fileName);

		/** �t�@�C�����݃`�F�b�N */
		if(existSettingFile(filePath, fileName)){
			file.delete();
		}

		/** �t�@�C���쐬 */
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true), FILE_CODE));
			bw.write(gmail + DLM + display + DLM + full + DLM + alarm + DLM + BUFFER_DATA + DLM + BUFFER_DATA + DLM + BUFFER_DATA);
			bw.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}



	/**
	 * �ݒ�t�@�C���ǂݍ��݃��\�b�h
	 * 
	 * �ݒ�t�@�C����ǂݍ���
	 * �f�[�^���ُ�ȏꍇreturn null
	 * 
	 * @param String, String
	 * @return�@String[]
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static String[] readSettingFile(String filePath, String fileName){
		File file = new File(filePath + fileName);

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), FILE_CODE));
			String line = new String(br.readLine());
			br.close();

			String[] resultStr = line.split(DLM, -1);

			/** �f�[�^���ڐ��`�F�b�N */
			if(resultStr.length == SETTING_DATA_NUM){
				return resultStr;
			}else{
				return null;
			}
		} catch (IOException e) {
			return null;
		}
	}

	
	
	/**
	 * �ݒ�t�@�C�����݃`�F�b�N
	 * 
	 * �ݒ�t�@�C�������݂��邩�`�F�b�N����
	 * ���݁Ftrue
	 * ���݂��Ȃ��Ffalse
	 * 
	 * @param String, String
	 * @return�@boolean
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static boolean existSettingFile(String filePath, String fileName){
		File file = new File(filePath + fileName);
		
		if(file.exists()){
			return true;
		}
		return false;
	}
}
