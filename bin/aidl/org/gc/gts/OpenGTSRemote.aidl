package org.gc.gts;


interface OpenGTSRemote {

	int loggingState();
    void startLogging();
    void pauseLogging();
    void resumeLogging();
	void stopLogging();

}