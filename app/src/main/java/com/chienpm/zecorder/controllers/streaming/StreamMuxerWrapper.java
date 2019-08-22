package com.chienpm.zecorder.controllers.streaming;
/*
 * ScreenRecordingSample
 * Sample project to cature and save audio from internal and video from screen as MPEG4 file.
 *
 * Copyright (c) 2014-2016 saki t_saki@serenegiant.com
 *
 * File name: MediaMuxerWrapper.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * All files in the folder are under this Apache License, Version 2.0.
 */

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import com.chienpm.zecorder.controllers.settings.VideoSetting;

import java.io.IOException;
import java.nio.ByteBuffer;

public class StreamMuxerWrapper {
	private static final boolean DEBUG = false;    // TODO set false on release
	private static final String TAG = "chienpm_log_stream";
	private final StreamProfile mStreamProfile;
	private final VideoSetting mVideoSetting;

//	private final SrsFlvMuxer mMuxer;    // API >= 18
	private final int mWidth, mHeight;
	private int mEncoderCount, mStatredCount;
	private boolean mIsStarted;
	private volatile boolean mIsPaused;
	private StreamEncoder mVideoEncoder, mAudioEncoder;
	private boolean useSoftEncoder = true;

	private MediaCodecInfo vmci;
	private boolean canSoftEncode;
	private String url = "rtmp://10.199.220.239/live/key";
	/**
	 * Constructor
	 *
	 * @param _ext extension of output file
	 * @throws IOException
	 */
	public StreamMuxerWrapper(final Context context, final StreamProfile streamProfile, VideoSetting videoSetting) throws IOException {

		mStreamProfile = streamProfile;

		mVideoSetting = videoSetting;
		mWidth = videoSetting.getWidth();
		mHeight = videoSetting.getHeight();
//		mMuxer = initMuxer();
	}

//	private SrsFlvMuxer initMuxer() {
//		SrsFlvMuxer mMuxer;
//		mMuxer = new SrsFlvMuxer(new RtmpHandler(mRtmpListener));
//		mEncoderCount = mStatredCount = 0;
//		mIsStarted = false;
//		//Todo: test strem
//		return mMuxer;
//	}

	public synchronized void prepare() throws IOException {
//		if(mMuxer!=null) {
//			mMuxer.start(url);
//			mMuxer.setVideoResolution(mWidth, mHeight);
//			if (mVideoEncoder != null)
//				mVideoEncoder.prepare();
//			if (mAudioEncoder != null)
//				mAudioEncoder.prepare();
//		}
	}

	public synchronized void startStreaming() {
		if (mVideoEncoder != null)
			mVideoEncoder.startStreaming();

		if (mAudioEncoder != null)
			mAudioEncoder.startStreaming();
	}

	public synchronized void stopStreaming() {
		if (mVideoEncoder != null)
			mVideoEncoder.stopStreaming();
		mVideoEncoder = null;
		if (mAudioEncoder != null)
			mAudioEncoder.stopStreaming();
		mAudioEncoder = null;
	}

	public synchronized boolean isStarted() {
		return mIsStarted;
	}

	public synchronized void pauseRecording() {
		mIsPaused = true;
		if (mVideoEncoder != null)
			mVideoEncoder.pauseStreaming();
		if (mAudioEncoder != null)
			mAudioEncoder.pauseStreaming();
	}

	public synchronized void resumeRecording() {
		if (mVideoEncoder != null)
			mVideoEncoder.resumeRecording();
		if (mAudioEncoder != null)
			mAudioEncoder.resumeRecording();
		mIsPaused = false;
	}

	public synchronized boolean isPaused() {
		return mIsPaused;
	}

//**********************************************************************
//**********************************************************************

	/**
	 * assign encoder to this calss. this is called from encoder.
	 *
	 * @param encoder instance of MediaVideoEncoderBase
	 */
	/*package*/
	void addEncoder(final StreamEncoder encoder) {
		if (encoder instanceof StreamVideoEncoderBase) {
			if (mVideoEncoder != null)
				throw new IllegalArgumentException("Video encoder already added.");
			mVideoEncoder = encoder;
		} else if (encoder instanceof StreamAudioEncoder) {
			if (mAudioEncoder != null)
				throw new IllegalArgumentException("Video encoder already added.");
			mAudioEncoder = encoder;
		} else
			throw new IllegalArgumentException("unsupported encoder");
		mEncoderCount = (mVideoEncoder != null ? 1 : 0) + (mAudioEncoder != null ? 1 : 0);
	}

	/**
	 * request start recording from encoder
	 *
	 * @return true when muxer is ready to write
	 */
	/*package*/
	synchronized boolean start() {
		if (DEBUG) Log.v(TAG, "start:");

		mStatredCount++;
		if ((mEncoderCount > 0) && (mStatredCount == mEncoderCount)) {

			mIsStarted = true;
			notifyAll();
			if (DEBUG) Log.v(TAG, "MediaMuxer started:");

		}
		return mIsStarted;
	}

	/**
	 * request stop recording from encoder when encoder received EOS
	 */
	/*package*/
	synchronized void stop() {
		if (DEBUG) Log.v(TAG, "stop:mStatredCount=" + mStatredCount);
		mStatredCount--;
		if ((mEncoderCount > 0) && (mStatredCount <= 0)) {
//			mMuxer.stop();
			mIsStarted = false;
			if (DEBUG) Log.v(TAG, "MediaMuxer stopped:");
		}
	}

	/**
	 * assign encoder to muxer
	 * @param format
	 * @return minus value indicate error
	 */
	/*package*/
	synchronized int addTrack(final MediaFormat format) {
//		if (mIsStarted)
//			throw new IllegalStateException("muxer already started");
//		final int trackIx = mMuxer.addTrack(format);
//		if (DEBUG) Log.i(TAG, "addTrack:trackNum=" + mEncoderCount + ",trackIx=" + trackIx + ",format=" + format);
//		return trackIx;
		return 0;
	}

	/**
	 * write encoded data to muxer
	 *
	 * @param trackIndex
	 * @param byteBuf
	 * @param bufferInfo
	 */
	/*package*/
	synchronized void writeSampleData(int trackIndex, final ByteBuffer byteBuf, final MediaCodec.BufferInfo bufferInfo) {
		if (mStatredCount > 0) {
//			mMuxer.writeSampleData(trackIndex, byteBuf, bufferInfo);
		}

	}

//	private final RtmpHandler.RtmpListener mRtmpListener = new RtmpHandler.RtmpListener() {
//		@Override
//		public void onRtmpConnecting(String msg) {
//			Log.i(TAG, "onRtmpConnecting: "+msg);
//		}
//
//		@Override
//		public void onRtmpConnected(String msg) {
//			Log.i(TAG, "onRtmpConnected: "+msg);
//		}
//
//		@Override
//		public void onRtmpVideoStreaming() {
//			Log.i(TAG, "on Rtmp Video Streaming ");
//		}
//
//		@Override
//		public void onRtmpAudioStreaming() {
//			Log.i(TAG, "on Rtmp Audio Streaming: ");
//		}
//
//		@Override
//		public void onRtmpStopped() {
//			Log.d(TAG, "onRtmpStopped: ");
//		}
//
//		@Override
//		public void onRtmpDisconnected() {
//			Log.i(TAG, "onRtmpDisconnected: ");
//		}
//
//		@Override
//		public void onRtmpVideoFpsChanged(double fps) {
//			Log.i(TAG, "onRtmpVideoFpsChanged: "+String.format("Output Fps: %f", fps));
//		}
//
//		@Override
//		public void onRtmpVideoBitrateChanged(double bitrate) {
//			int rate = (int) bitrate;
//			if (rate / 1000 > 0) {
//				Log.i(TAG, String.format("Video bitrate: %f kbps", bitrate / 1000));
//			} else {
//				Log.i(TAG, String.format("Video bitrate: %d bps", rate));
//			}
//		}
//
//		@Override
//		public void onRtmpAudioBitrateChanged(double bitrate) {
//			int rate = (int) bitrate;
//			if (rate / 1000 > 0) {
//				Log.i(TAG, String.format("Audio bitrate: %f kbps", bitrate / 1000));
//			} else {
//				Log.i(TAG, String.format("Audio bitrate: %d bps", rate));
//			}
//		}
//
//		@Override
//		public void onRtmpSocketException(SocketException e) {
//			handleException(e);
//		}
//
//		@Override
//		public void onRtmpIOException(IOException e) {
//			handleException(e);
//		}
//
//		@Override
//		public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
//			handleException(e);
//		}
//
//		@Override
//		public void onRtmpIllegalStateException(IllegalStateException e) {
//			handleException(e);
//		}
//	};

	private void handleException(Exception e) {
		try {
			Log.e(TAG, "handleException: ", e);
			stop();
			stopStreaming();
		} catch (Exception e1) {
			//
		}
	}

//	public AtomicInteger getVideoFrameCacheNumber() {
//		return mMuxer.getVideoFrameCacheNumber();
//	}

//**********************************************************************
//**********************************************************************
}
