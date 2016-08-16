package com.curry.vendingmachine.serialport;

public class SerialPortCommands {
	// rececive
	public static final String COMMAND_RECEIVE_KEY_BOARD_HEAD = "0251";
	public static final String COMMAND_RECEIVE_KEY_BOARD_KEY_0 = "0251000053";
	public static final String COMMAND_RECEIVE_KEY_BOARD_KEY_1 = "0251010054";
	public static final String COMMAND_RECEIVE_KEY_BOARD_KEY_2 = "0251020055";
	public static final String COMMAND_RECEIVE_KEY_BOARD_KEY_3 = "0251030056";
	public static final String COMMAND_RECEIVE_KEY_BOARD_KEY_4 = "0251040057";
	public static final String COMMAND_RECEIVE_KEY_BOARD_KEY_5 = "0251050058";
	public static final String COMMAND_RECEIVE_KEY_BOARD_KEY_6 = "0251060059";
	public static final String COMMAND_RECEIVE_KEY_BOARD_KEY_7 = "025107005A";
	public static final String COMMAND_RECEIVE_KEY_BOARD_KEY_8 = "025108005B";
	public static final String COMMAND_RECEIVE_KEY_BOARD_KEY_9 = "025109005C";
	public static final String COMMAND_RECEIVE_KEY_BOARD_KEY_CONFIRM = "02510A005D";
	public static final String COMMAND_RECEIVE_KEY_BOARD_KEY_CANCEL = "02510B005E";
	public static final String COMMAND_RECEIVE_KEY_BOARD_KEY_SETTINGS = "02510C005F";
	public static final String COMMAND_RECEIVE_KEY_BOARD_KEY_RETURN_CHANGE = "02510D0060";

	public static final String COMMAND_RECEIVE_LOCK_HEAD = "0152";
	public static final String COMMAND_RECEIVE_MOTOR_HEAD = "0153";

	public static final String COMMAND_RECEIVE_GSM_INIT_ONE_OK = "41542B51494647434E543D300D0D0A4F4B0D0A";
	public static final String COMMAND_RECEIVE_GSM_INIT_TWO_OK = "41542B5149435347503D312C22434D4E4554220D0D0A4F4B0D0A";
	public static final String COMMAND_RECEIVE_GSM_INIT_THREE_OK = "41542B51495245474150500D0D0A4F4B0D0A";
	public static final String COMMAND_RECEIVE_GSM_OK_SIGN = "0D0A4F4B0D0A";
	public static final String COMMAND_RECEIVE_GSM_QUIT_OK = "0D0A4445414354204F4B0D0A";
	public static final String COMMAND_RECEIVE_GSM_OK_SIGN_DOUBLE = "0D0A0D0A4F4B0D0A";
	public static final String COMMAND_RECEIVE_GSM_ERROR_SIGN = "4552524F52";
	// TODO
	// public static final String COMMAND_RECEIVE_GSM_ACT_OK = "0D0A4F4B0D0A";
	public static final String COMMAND_RECEIVE_GSM_HTTP_URL_CONNECT_OK_PREFIX = "41542B514854545055524C3D";
	public static final String COMMAND_RECEIVE_GSM_HTTP_URL_CONNECT_OK_SUFFIX = "2C33300D0D0A434F4E4E4543540D0A";
	// public static final String COMMAND_RECEIVE_GSM_HTTP_URL_OK =
	// "0D0A4F4B0D0A";
	public static final String COMMAND_RECEIVE_GSM_CONNECT_SIGN = "0D0A434F4E4E4543540D0A";// CONNECT
	public static final String COMMAND_RECEIVE_GSM_0 = "300D0A0D0A";
	public static final String COMMAND_RECEIVE_GSM_BRACKET_START = "7B";
	public static final String COMMAND_RECEIVE_GSM_BRACKET_END = "7D";
	// send
	public static final String COMMAND_SEND_GSM_INIT_ONE = "AT+QIFGCNT=0\r\n";
	public static final String COMMAND_SEND_GSM_INIT_TWO = "AT+QICSGP=1,\"CMNET\"\r\n";
	public static final String COMMAND_SEND_GSM_INIT_THREE = "AT+QIREGAPP\r\n";
	public static final String COMMAND_SEND_GSM_ACT = "AT+QIACT\r\n";
	public static final String COMMAND_SEND_GSM_QUIT = "AT+QIDEACT\r\n";
	// AT+QHTTPURL=58,30
	public static final String COMMAND_SEND_GSM_HTTP_URL_HEAD = "AT+QHTTPURL=";
	public static final String COMMAND_SEND_GSM_HTTP_POST_HEAD = "AT+QHTTPPOST=";
	public static final String COMMAND_SEND_GSM_HTTP_GET = "AT+QHTTPGET=60\r\n";
	public static final String COMMAND_SEND_GSM_HTTP_READ = "AT+QHTTPREAD=30\r\n";

	// SIM
	public static final String COMMAND_SEND_GSM_SIGNAL = "AT+CSQ\r\n";
	public static final String COMMAND_SEND_GSM_SIM_STATUS = "AT+QNSTATUS\r\n";
	public static final String COMMAND_GET_IMEI = "AT+GSN\r\n";
}