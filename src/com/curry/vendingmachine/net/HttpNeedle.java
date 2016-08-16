package com.curry.vendingmachine.net;

import java.io.OutputStream;
import java.util.ArrayList;

import android.os.Handler;

import com.curry.vendingmachine.helper.SerialDataSendHelper;
import com.curry.vendingmachine.net.needle.Needle;
import com.curry.vendingmachine.serializable.Parser;
import com.curry.vendingmachine.serialport.SerialPortCommands;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.CurryUtils;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.NameValuePair;
import com.curry.vendingmachine.utils.TypeUtil;

/**
 * Quectel M35 GSM HTTP Command
 * 
 * @author wushuang
 * @param <T>
 */
public class HttpNeedle<T> {
	public static final String TAG = HttpNeedle.class.getSimpleName();

	private HttpState httpState = HttpState.HTTP_STATE_OFFLINE;

	private boolean isPost = false;

	public enum HttpState {
		HTTP_STATE_GET_IMEI, HTTP_STATE_CHECK_SIM_SIGNAL, HTTP_STATE_CHECK_SIM_STATUS, HTTP_STATE_OFFLINE, HTTP_STATE_INIT_STEP_1, HTTP_STATE_INIT_STEP_2, HTTP_STATE_INIT_STEP_3, HTTP_STATE_INIT_STEP_ACT, HTTP_STATE_ACT_OK, HTTP_STATE_POST_CONNECT_URL, HTTP_STATE_POST_SET_URL, HTTP_STATE_POST_CONNECT_POST, HTTP_STATE_POST_PARAMS, HTTP_STATE_POST_READ, HTTP_STATE_POST_FREE, HTTP_STATE_QUIT
	}

	private OutputStream mOutputStreamGSM;
	private OnHttpResponseListener<T> mListener;
	private OnHttpErrorListener mErrorListener;
	private Class<T> mClassName;
	private ArrayList<NameValuePair> mParams;
	private String mUrl;
	private StringBuffer urlConnectOk;
	private StringBuffer strBuffer;
	private int urlSize;
	private StringBuffer postParam;
	private int paramSize;
	private boolean isReadStart = false;
	private StringBuffer responseJsonStr;
	private OnHttpSIMListener mOnHttpListener;
	private OnHttpInitListener mOnInitListner;
	private OnHttpProcessListener mOnHttpProcessListener;
	private boolean isQr = false;

	private HttpNeedle() {

	}

	public static HttpNeedle getInstance() {
		return Holder.INSTANCE;
	}

	private static class Holder {
		static final HttpNeedle INSTANCE = new HttpNeedle();
	}

	public void initOutPutStream(OutputStream outputStream) {
		if (this.mOutputStreamGSM == null) {
			this.mOutputStreamGSM = outputStream;
		}
	}

	public void setHttpProcessListener(OnHttpProcessListener listener) {
		this.mOnHttpProcessListener = listener;
	}

	public void setPostIsQr(boolean isQr) {
		this.isQr = isQr;
	}

	// Needle.onBackgroundThread().withTaskType(NEEDLE_HTTP_TYPE).serially().execute(new
	// Runnable() {
	//
	// @Override
	// public void run() {
	// }
	// });

	/**
	 * initGPRS
	 * 
	 * @param outputStreamGSM
	 *            can not be null
	 */
	private void initGprsNeedlely(final OutputStream outputStreamGSM,
			OnHttpInitListener onInitListner) {
		this.mOnInitListner = onInitListner;
		Needle.onBackgroundThread().withTaskType(Constants.NEEDLE_TYPE_HTTP)
				.serially().execute(new Runnable() {

					@Override
					public void run() {
						initGprsStep1(outputStreamGSM);
					}
				});
	}

	private void initGprsNeedlely(final OutputStream outputStreamGSM) {
		Needle.onBackgroundThread().withTaskType(Constants.NEEDLE_TYPE_HTTP)
				.serially().execute(new Runnable() {

					@Override
					public void run() {
						initGprsStep1(outputStreamGSM);
					}
				});
	}

	/**
	 * HTTP POST
	 * 
	 * @param url
	 * @param params
	 * @param listener
	 * @param errorListener
	 * @param className
	 */
	public void postNeedlely(final String url,
			final ArrayList<NameValuePair> params,
			final OnHttpResponseListener<T> listener,
			final OnHttpErrorListener errorListener, final Class<T> className) {
		L.wtf("isPost", "postNeedlely isPost=" + isPost);
		if (!isPost) {

			isPost = true;

			initGprsNeedlely(mOutputStreamGSM, new OnHttpInitListener() {

				@Override
				public void onInitSuc() {
					// TODO Auto-generated method stub
					Needle.onBackgroundThread()
							.withTaskType(Constants.NEEDLE_TYPE_HTTP)
							.serially().execute(new Runnable() {

								@Override
								public void run() {
									post(url, params, listener, errorListener,
											className);
								}
							});
				}
			});
		} else {
			// TODO 两个命令连续时 可用
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					postNeedlely(url, params, listener, errorListener,
							className);
				}
			}, 1000);
		}
	}

	/**
	 * parse data from GSM serial port
	 * 
	 * @param buffer
	 * @param size
	 */
	public void parseDataNeedlely(final byte[] buffer, final int size) {
		Needle.onBackgroundThread().withTaskType(Constants.NEEDLE_TYPE_HTTP)
				.serially().execute(new Runnable() {

					@Override
					public void run() {
						parseData(buffer, size);
					}
				});
	}

	public void quitGPRSNeedlely() {
		httpState = HttpState.HTTP_STATE_QUIT;
		sendCommandToGPRSNeedlely(SerialPortCommands.COMMAND_SEND_GSM_QUIT);
	}

	public void getIMEI(OnHttpSIMListener onHttpListener,
			OutputStream outputStreamGSM) {
		if (this.mOutputStreamGSM == null) {
			this.mOutputStreamGSM = outputStreamGSM;
		}
		this.mOnHttpListener = onHttpListener;
		httpState = HttpState.HTTP_STATE_GET_IMEI;
		sendCommandToGPRSNeedlely(SerialPortCommands.COMMAND_GET_IMEI);
	}

	public void checkSIMStatus(OnHttpSIMListener onHttpListener) {
		this.mOnHttpListener = onHttpListener;
		httpState = HttpState.HTTP_STATE_CHECK_SIM_STATUS;
		sendCommandToGPRSNeedlely(SerialPortCommands.COMMAND_SEND_GSM_SIM_STATUS);
	}

	public void checkSimSignal(OnHttpSIMListener onHttpListener) {
		this.mOnHttpListener = onHttpListener;
		httpState = HttpState.HTTP_STATE_CHECK_SIM_SIGNAL;
		sendCommandToGPRSNeedlely(SerialPortCommands.COMMAND_SEND_GSM_SIGNAL);
	}

	private byte[] buffertemp = new byte[34];
	private int offset = 0;

	private void clearTempData() {
		offset = 0;
		buffertemp = new byte[34];
	}

	private void parseData(byte[] buffer, int size) {
		byte[] realBuf = CurryUtils.getRealBuffer(buffer, size);
		String hexStr = TypeUtil.bytesToHex(realBuf);
		L.d("http", "parseData hexStr:" + hexStr);
		L.d("http", "parseData str:" + new String(buffer, 0, size));
		L.d("http", "parseData httpState:" + httpState);
		if (httpState == HttpState.HTTP_STATE_GET_IMEI) {
			// TODO
			if (hexStr
					.contains(SerialPortCommands.COMMAND_RECEIVE_GSM_ERROR_SIGN)) {
				getIMEI(mOnHttpListener, mOutputStreamGSM);
			} else {
				httpState = HttpState.HTTP_STATE_POST_FREE;
				L.wtf("IMEI", "IMEI:" + hexStr);
				String headStr = TypeUtil
						.str2HexStr(SerialPortCommands.COMMAND_GET_IMEI);

				L.d("IMEI", "headStr:" + headStr + "len1:" + headStr.length());
				L.d("IMEI", "totalLen:" + hexStr.length());
				L.d("IMEI", "size:" + size);
				L.d("IMEI",
						"len2:"
								+ SerialPortCommands.COMMAND_RECEIVE_GSM_OK_SIGN_DOUBLE
										.length());

				String imeiHex = hexStr
						.substring(
								headStr.length() + 2,
								hexStr.length()
										- SerialPortCommands.COMMAND_RECEIVE_GSM_OK_SIGN_DOUBLE
												.length());

				String imei = TypeUtil.hexStr2Str(imeiHex);
				if (mOnHttpListener != null) {
					mOnHttpListener.onGetIMEI(imei);
				}
			}
		} else if (httpState == HttpState.HTTP_STATE_CHECK_SIM_STATUS) {
			L.d("sim", "sim state hexStr:" + hexStr + "size:" + size);
			if (hexStr
					.contains(SerialPortCommands.COMMAND_RECEIVE_GSM_ERROR_SIGN)
					|| hexStr.contains("4E4F542052454144590D0A")) {// NOT READY
				clearTempData();
				if (mOnHttpListener != null) {
					mOnHttpListener.onSimStatusCheck(-1);
				}
			} else {
				if (size < 34) {// totalSize是这个命令返回的指令长度
					// buffertemp = new byte[34];
					if (offset < 34) {
						for (int i = 0; i < size; i++) {
							if (i + offset < 34) {
								buffertemp[i + offset] = realBuf[i];
							} else {
								break;
							}
						}
						offset += size;
						if (offset < 34) {
							return;
						} else {
							L.d("34", "has reach totalSize length just now:"
									+ offset);
						}
					} else {
						L.d("34", "has reach totalSize length now:" + offset);
					}
				} else {
					L.d("34", "more than 34");
				}

				httpState = HttpState.HTTP_STATE_POST_FREE;

				L.d("34", "offset:" + offset + "buffertemp.length:"
						+ buffertemp.length);
				L.d("34", "buffertemp:" + TypeUtil.bytesToHex(buffertemp));

				for (int i = 0; i < buffertemp.length; i++) {
					L.d("34",
							"buffertemp[" + i + "]:"
									+ TypeUtil.byteToInt(buffertemp[i]));
					switch (TypeUtil.byteToInt(buffertemp[i])) {
					case 0x3A:
						int hexStatus = TypeUtil
								.byteToIntHex(buffertemp[i + 2]);
						L.d("sim", "hexStatus:" + hexStatus);

						clearTempData();

						if (mOnHttpListener != null) {
							mOnHttpListener
									.onSimStatusCheck(Integer.parseInt(String
											.valueOf((char) hexStatus)));
						}
						return;
					default:
						break;
					}
				}

				clearTempData();
			}

		} else if (hexStr
				.contains(SerialPortCommands.COMMAND_RECEIVE_GSM_ERROR_SIGN)) {
			L.d("http", "error:" + hexStr);
			isPost = false;
			// TODO
			if (mErrorListener != null) {
				mErrorListener.onError(httpState + hexStr);
			}
			httpState = HttpState.HTTP_STATE_POST_FREE;
		} else if (httpState == HttpState.HTTP_STATE_CHECK_SIM_SIGNAL) {
			httpState = HttpState.HTTP_STATE_POST_FREE;
			L.d("sim", "sim signal hexStr:" + hexStr);
			// :后面,前面的字节
			int startPos = 0;
			int endPos = 0;
			for (int i = 0; i < realBuf.length; i++) {
				switch (TypeUtil.byteToInt(realBuf[i])) {
				case 0x3A:
					startPos = i + 2;
					break;
				case 0x2C:
					endPos = i - 1;
					break;
				default:
					break;
				}
			}
			L.d("SIM", "startPos:" + startPos + "endPos:" + endPos);
			if (endPos < startPos || (startPos == 0 && endPos == 0)) {
				L.d("SIM", "check sim signal no :, data");
				return;
			}
			StringBuffer sb = new StringBuffer();
			for (int j = startPos; j <= endPos; j++) {
				int hex = TypeUtil.byteToIntHex(realBuf[j]);
				sb.append((char) hex);
			}
			L.d("SIM", "sb:" + sb.toString());
			if (mOnHttpListener != null) {
				mOnHttpListener
						.onSimSignalCheck(Integer.parseInt(sb.toString()));
			}
		} else if (httpState == HttpState.HTTP_STATE_POST_READ
				&& hexStr
						.contains(SerialPortCommands.COMMAND_RECEIVE_GSM_CONNECT_SIGN)
				&& SerialPortCommands.COMMAND_RECEIVE_GSM_BRACKET_START
						.equals(hexStr.substring(hexStr.length() - 2,
								hexStr.length()))) {
			L.wtf("http", "isReadStart");
			L.d("http", "isReadStart");
			isReadStart = true;
			if (mOnHttpProcessListener != null && isQr) {
				mOnHttpProcessListener.onReadStart();
			}
			responseJsonStr = CurryUtils.newStringBuffer(responseJsonStr);
			responseJsonStr.append("{");
		} else if (isReadStart
				&& SerialPortCommands.COMMAND_RECEIVE_GSM_OK_SIGN
						.equals(hexStr)) {
			L.wtf("http", "isRead ok");
			isReadStart = false;
			L.wtf("http",
					"isRead ok response str json:" + responseJsonStr.toString());
			if (mListener != null) {
				mListener.onResponse(Parser.parse(responseJsonStr.toString(),
						mClassName));
			}
			quitGPRSNeedlely();

		} else if (isReadStart
				&& !SerialPortCommands.COMMAND_RECEIVE_GSM_0.equals(hexStr
						.substring(hexStr.length() - 10, hexStr.length()))) {
			L.wtf("http", "isReading");
			// TODO bug length可能<10
			String str = new String(buffer, 0, size);
			responseJsonStr.append(str);
		} else if (isReadStart
				&& SerialPortCommands.COMMAND_RECEIVE_GSM_0.equals(hexStr
						.substring(hexStr.length() - 10, hexStr.length()))) {
			L.wtf("http", "isRead end");
			L.d("http", "isRead end");
			isReadStart = false;
			// 7D(383738463839227D0D0A300D0A0D0A or 300D0A0D0A)
			if (hexStr
					.contains(SerialPortCommands.COMMAND_RECEIVE_GSM_BRACKET_END)) {
				L.wtf("http", "end 7D");
				byte b[] = new byte[size - 7];
				for (int i = 0; i < (size - 7); i++) {
					b[i] = buffer[i];
				}
				String str = new String(b, 0, size - 7);
				L.wtf("http", "end Str:" + str);
				responseJsonStr.append(str);
			}
			L.wtf("http", "response str json:" + responseJsonStr.toString());
			if (mListener != null) {
				mListener.onResponse(Parser.parse(responseJsonStr.toString(),
						mClassName));
			}
		} else if (SerialPortCommands.COMMAND_RECEIVE_GSM_INIT_ONE_OK
				.equals(hexStr)) {
			L.wtf("http", "init one ok");
			initGprsStep2();
		} else if (SerialPortCommands.COMMAND_RECEIVE_GSM_INIT_TWO_OK
				.equals(hexStr)) {
			L.wtf("http", "init two ok");

			// 去除了step 3和act 直接成功
			if (mOnInitListner != null) {
				mOnInitListner.onInitSuc();
			}
			// TODO
			// initGprsStep3();
		} else if (SerialPortCommands.COMMAND_RECEIVE_GSM_INIT_THREE_OK
				.equals(hexStr)) {
			L.wtf("http", "init three ok");
			initGprsAct();
		} else if (SerialPortCommands.COMMAND_RECEIVE_GSM_OK_SIGN
				.equals(hexStr)) {
			L.wtf("http", "OK httpState:" + httpState);
			if (httpState == HttpState.HTTP_STATE_INIT_STEP_ACT) {
				L.wtf("http", "act ok");
				// save init gprs success flag
				httpState = HttpState.HTTP_STATE_ACT_OK;
				if (mOnInitListner != null) {
					mOnInitListner.onInitSuc();
				}
				// saveInitGprsSuc();
			} else if (httpState == HttpState.HTTP_STATE_POST_SET_URL) {
				L.wtf("http", "http url ok");
				postConnectPost();
			} else if (httpState == HttpState.HTTP_STATE_POST_PARAMS) {
				L.wtf("http", "http post param ok");
				L.d("http", "http post param ok");
				postRead();
			} else if (httpState == HttpState.HTTP_STATE_POST_READ) {
				L.wtf("http", "http read ok");
				// TODO
				quitGPRSNeedlely();
			} else if (httpState == HttpState.HTTP_STATE_QUIT) {
				L.wtf("http", "http quit ok");
				httpState = HttpState.HTTP_STATE_POST_FREE;
				isPost = false;
				// TODO
			}
		} else if (!CurryUtils.isStringEmpty(urlConnectOk)
				&& urlConnectOk.toString().equals(hexStr)) {
			L.wtf("http", "http url connect ok");
			postSetUrl();
		} else if (SerialPortCommands.COMMAND_RECEIVE_GSM_CONNECT_SIGN
				.equals(hexStr)) {
			L.wtf("http", "http post connect ok");
			postParams();
		} else if (SerialPortCommands.COMMAND_RECEIVE_GSM_QUIT_OK
				.equals(hexStr)) {
			L.wtf("http", "http quit deact ok");
			httpState = HttpState.HTTP_STATE_POST_FREE;
			isPost = false;
			// TODO
		}

	}

	private void initGprsStep1(OutputStream outputStreamGSM) {
		httpState = HttpState.HTTP_STATE_INIT_STEP_1;

		if (outputStreamGSM == null) {
			L.e(TAG, "outputStreamGSM null");
		}

		if (this.mOutputStreamGSM == null) {
			this.mOutputStreamGSM = outputStreamGSM;
		}

		SerialDataSendHelper.getInstance().sendCommand(mOutputStreamGSM,
				SerialPortCommands.COMMAND_SEND_GSM_INIT_ONE);
	}

	private void initGprsStep2() {
		httpState = HttpState.HTTP_STATE_INIT_STEP_2;
		sendCommandToGPRSNeedlely(SerialPortCommands.COMMAND_SEND_GSM_INIT_TWO);
	}

	private void initGprsStep3() {
		httpState = HttpState.HTTP_STATE_INIT_STEP_3;
		sendCommandToGPRSNeedlely(SerialPortCommands.COMMAND_SEND_GSM_INIT_THREE);
	}

	private void initGprsAct() {
		httpState = HttpState.HTTP_STATE_INIT_STEP_ACT;
		sendCommandToGPRSNeedlely(SerialPortCommands.COMMAND_SEND_GSM_ACT);
	}

	private void post(String url, ArrayList<NameValuePair> params,
			OnHttpResponseListener<T> listener,
			OnHttpErrorListener errorListener, Class<T> className) {
		this.mParams = params;
		this.mListener = listener;
		this.mErrorListener = errorListener;
		this.mClassName = className;

		L.d("post url:" + url);
		if (mOnHttpProcessListener != null && isQr) {
			mOnHttpProcessListener.onPostStart();
		}
		// for (NameValuePair pair : params) {
		// L.d("post params:" + pair.getName() + ":" + pair.getValue());
		// }

		if (mParams == null || mParams.size() <= 0) {
			L.e(TAG, "need params");
			return;
		}

		postConnectUrl(url);
	}

	private void postConnectUrl(String url) {
		httpState = HttpState.HTTP_STATE_POST_CONNECT_URL;

		this.mUrl = url;
		urlSize = url.length();

		urlConnectOk = CurryUtils.newStringBuffer(urlConnectOk);

		strBuffer = CurryUtils.newStringBuffer(strBuffer);
		strBuffer.append(SerialPortCommands.COMMAND_SEND_GSM_HTTP_URL_HEAD);
		strBuffer.append(urlSize);
		strBuffer.append(",30\r\n");

		urlConnectOk
				.append(SerialPortCommands.COMMAND_RECEIVE_GSM_HTTP_URL_CONNECT_OK_PREFIX);
		urlConnectOk.append(TypeUtil.bytesToHex(String.valueOf(urlSize)
				.getBytes()));
		urlConnectOk
				.append(SerialPortCommands.COMMAND_RECEIVE_GSM_HTTP_URL_CONNECT_OK_SUFFIX);
		L.d("urlConnectOk:" + urlConnectOk);

		SerialDataSendHelper.getInstance().sendCommand(mOutputStreamGSM,
				strBuffer.toString());

	}

	private void postSetUrl() {
		httpState = HttpState.HTTP_STATE_POST_SET_URL;
		strBuffer = CurryUtils.newStringBuffer(strBuffer);
		strBuffer.append(mUrl);
		strBuffer.append("\r\n");

		sendCommandToGPRSNeedlely(strBuffer.toString());
	}

	private void postConnectPost() {
		httpState = HttpState.HTTP_STATE_POST_CONNECT_POST;
		if (mParams != null && mParams.size() > 0) {
			obtainParam();
			L.d("post param:" + postParam.toString());
			paramSize = postParam.toString().length();

			strBuffer = CurryUtils.newStringBuffer(strBuffer);
			strBuffer
					.append(SerialPortCommands.COMMAND_SEND_GSM_HTTP_POST_HEAD);
			strBuffer.append(paramSize);
			strBuffer.append(",50,10");
			strBuffer.append("\r\n");

			sendCommandToGPRSNeedlely(strBuffer.toString());

		} else {
			L.d("post params empty");
		}
	}

	private void obtainParam() {
		if (mParams != null && mParams.size() > 0) {
			NameValuePair pair;
			postParam = new StringBuffer();
			for (int i = 0; i < mParams.size(); i++) {
				pair = mParams.get(i);

				postParam.append(pair.getName());
				postParam.append("=");
				postParam.append(pair.getValue());
				if (i < mParams.size() - 1) {
					postParam.append("&");
				}
			}
		}
	}

	private void postParams() {
		httpState = HttpState.HTTP_STATE_POST_PARAMS;
		strBuffer = CurryUtils.newStringBuffer(strBuffer);
		strBuffer.append(postParam.toString());
		strBuffer.append("\r\n");
		sendCommandToGPRSNeedlely(strBuffer.toString());
	}

	private void postRead() {
		httpState = HttpState.HTTP_STATE_POST_READ;
		sendCommandToGPRSNeedlely(SerialPortCommands.COMMAND_SEND_GSM_HTTP_READ);
	}

	private void sendCommandToGPRSNeedlely(final String command) {
		Needle.onBackgroundThread().withTaskType(Constants.NEEDLE_TYPE_HTTP)
				.serially().execute(new Runnable() {

					@Override
					public void run() {
						SerialDataSendHelper.getInstance().sendCommand(
								mOutputStreamGSM, command);
					}
				});
	}

	public interface OnHttpResponseListener<T> {
		void onResponse(T bean);
	}

	public interface OnHttpProcessListener {
		void onPostStart();

		void onReadStart();
	}

	public interface OnHttpErrorListener {
		void onError(String error);
	}

	public interface OnHttpInitListener {
		void onInitSuc();
	}

	public interface OnHttpSIMListener {
		void onSimSignalCheck(int asu);

		void onSimStatusCheck(int status);

		void onGetIMEI(String imei);
	}

}
