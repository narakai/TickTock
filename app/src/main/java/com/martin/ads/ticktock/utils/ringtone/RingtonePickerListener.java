/*
 * Copyright 2017 Keval Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance wit
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 *  the specific language governing permissions and limitations under the License.
 */

package com.martin.ads.ticktock.utils.ringtone;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Keval on 29-Mar-17.
 */

public interface RingtonePickerListener extends Serializable {
    void OnRingtoneSelected(String ringtoneName, Uri ringtoneUri);
}
