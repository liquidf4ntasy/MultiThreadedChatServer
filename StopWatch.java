/**
// Name: Satyajit Deshmukh
// UTA ID: 1001417727
// Lab 2 - CSE 5306 - 2 Phase Commit Protocol
*/

public class StopWatch {
    // Constructor
    public StopWatch() {
    }

    // Public API
    public void start() {
        if (!_isRunning) {
            _startTime = System.nanoTime();
            _isRunning = true;
        }
    }

    public void stop() {
        if (_isRunning) {
            _elapsedTime += System.nanoTime() - _startTime;
            _isRunning = false;
        }
    }

    public void reset() {
        _elapsedTime = 0;
        if (_isRunning) {
            _startTime = System.nanoTime();
        }
    }

    public boolean isRunning() {
        return _isRunning;
    }

    public long getElapsedTimeNanos() {
        if (_isRunning) {
            return System.nanoTime() - _startTime;
        }
        return _elapsedTime;
    }

    public long getElapsedTimeSeconds() {
        return getElapsedTimeNanos() / 1000000000L;
    }

    // Private Members
    private boolean _isRunning = false;
    private long _startTime = 0;
    private long _elapsedTime = 0;
}