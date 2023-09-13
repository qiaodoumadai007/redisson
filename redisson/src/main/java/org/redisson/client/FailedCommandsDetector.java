/**
 * Copyright (c) 2013-2022 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson.client;

import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Detects failed Redis node if it has certain amount of command execution errors
 * <code>failedCommandsLimit</code> in <code>checkInterval</code> time interval.
 *
 * @author Nikita Koksharov
 *
 */
public class FailedCommandsDetector implements FailedNodeDetector {

    private final long checkInterval;

    private long failedCommandsLimit;

    private final NavigableSet<Long> failedCommands = new ConcurrentSkipListSet<>();

    public FailedCommandsDetector(long checkInterval, int failedCommandsLimit) {
        this.checkInterval = checkInterval;
        this.failedCommandsLimit = failedCommandsLimit;
    }

    @Override
    public void onConnectFailed() {
    }

    @Override
    public void onConnectSuccessful() {
    }

    @Override
    public void onPingSuccessful() {
    }

    @Override
    public void onCommandSuccessful() {
    }

    @Override
    public void onPingFailed() {
    }

    @Override
    public void onCommandFailed(Throwable cause) {
        failedCommands.add(System.currentTimeMillis());
    }

    @Override
    public boolean isNodeFailed() {
        long start = System.currentTimeMillis() - checkInterval;
        failedCommands.headSet(start).clear();

        if (failedCommands.tailSet(start).size() >= failedCommandsLimit) {
            failedCommands.clear();
            return true;
        }
        return false;
    }

}
