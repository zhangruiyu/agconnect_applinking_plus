/*
    Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License")
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.huawei.agc.flutter.applinking.viewModel;


import android.app.Activity;
import android.net.Uri;

import com.huawei.agc.flutter.applinking.utils.ReplyHandler;
import com.huawei.agc.flutter.applinking.utils.Utils;
import com.huawei.agc.flutter.applinking.utils.ValueGetter;
import com.huawei.agconnect.applinking.AGConnectAppLinking;
import com.huawei.agconnect.applinking.AppLinking;
import com.huawei.agconnect.applinking.ShortAppLinking;
import com.huawei.agconnect.applinking.ShortAppLinking.LENGTH;
import com.huawei.agconnect.exception.AGCException;

import java.util.HashMap;
import java.util.Map;

import io.flutter.Log;
import io.flutter.plugin.common.MethodCall;

public class AppLinkingViewModel {
    private static final String TAG = AppLinkingViewModel.class.getSimpleName();

    private ReplyHandler replyHandler;

    public void setReplyHandler(ReplyHandler replyHandler) {
        this.replyHandler = replyHandler;
    }

    public void buildShortAppLinking() {
        ShortAppLinking.LENGTH shortAppLinkingLength;

        if (replyHandler.getCall().argument("shortAppLinkingLength") != null) {
            shortAppLinkingLength = ShortAppLinking.LENGTH.valueOf(replyHandler.getCall().argument("shortAppLinkingLength"));
        } else {
            shortAppLinkingLength = LENGTH.SHORT;
        }
        createAppLinkingWithInfo().buildShortAppLinking(shortAppLinkingLength).addOnSuccessListener(shortAppLinking -> {
            replyHandler.success(Utils.fromShortAppLinkingToMap(shortAppLinking));
        }).addOnFailureListener(e -> {
            if (e instanceof AGCException) {
                replyHandler.error("", ((AGCException) e).getErrMsg(), e.getLocalizedMessage());
                Log.e(TAG, e.getMessage());
            } else {
                replyHandler.error("", e.getLocalizedMessage(), e.getMessage());
            }
        });
    }

    public void buildLongAppLinking() {
        final AppLinking appLinking = createAppLinkingWithInfo().buildAppLinking();
        replyHandler.success(Utils.fromAppLinkingToMap(appLinking));
    }

    private AppLinking.SocialCardInfo createSocialCardInfo(Map<String, Object> map) {
        final AppLinking.SocialCardInfo.Builder builder = AppLinking.SocialCardInfo.newBuilder();

        if (map.get("description") != null) {
            builder.setDescription(ValueGetter.getString("description", map));
        }
        if (map.get("imageUrl") != null) {
            builder.setImageUrl(ValueGetter.getString("imageUrl", map));
        }
        if (map.get("title") != null) {
            builder.setTitle(ValueGetter.getString("title", map));
        }
        return builder.build();
    }

    private AppLinking.CampaignInfo createCampaignInfo(Map<String, Object> map) {
        final AppLinking.CampaignInfo.Builder builder = AppLinking.CampaignInfo.newBuilder();

        if (map.get(("medium")) != null) {
            builder.setMedium(ValueGetter.getString("medium", map));
        }
        if (map.get("name") != null) {
            builder.setName(ValueGetter.getString("name", map));
        }
        if (map.get("source") != null) {
            builder.setSource(ValueGetter.getString("source", map));
        }
        return builder.build();
    }

    private AppLinking.AndroidLinkInfo createAndroidLinkInfo(Map<String, Object> map) {
        final AppLinking.AndroidLinkInfo.Builder builder;

        if (map.get("androidPackageName") != null) {
            builder = AppLinking.AndroidLinkInfo.newBuilder(ValueGetter.getString("androidPackageName", map));
        } else {
            builder = AppLinking.AndroidLinkInfo.newBuilder();
        }
        if (map.get(("androidDeepLink")) != null) {
            builder.setAndroidDeepLink(ValueGetter.getString("androidDeepLink", map));
        }
        if (map.get("androidOpenType") != null) {
            builder.setOpenType(
                    AppLinking.AndroidLinkInfo.AndroidOpenType.valueOf(ValueGetter.getString("androidOpenType", map)));
            if ("CustomUrl".equals(ValueGetter.getString("androidOpenType", map))
                    && map.get("androidFallbackUrl") != null) {
                builder.setFallbackUrl(ValueGetter.getString("androidFallbackUrl", map));
            }
        }
        return builder.build();
    }

    private AppLinking.IOSLinkInfo createIOSLinkInfo(Map<String, Object> mapIos, Map<String, Object> mapTunes) {
        final AppLinking.IOSLinkInfo.Builder builder;

        builder = AppLinking.IOSLinkInfo.newBuilder();
        if (mapIos.get(("iosBundleId")) != null) {
            builder.setBundleId(ValueGetter.getString("iosBundleId", mapIos));
        }
        if (mapIos.get(("iosDeepLink")) != null) {
            builder.setIOSDeepLink(ValueGetter.getString("iosDeepLink", mapIos));
        }
        if (mapIos.get(("iosFallbackUrl")) != null) {
            builder.setFallbackUrl(ValueGetter.getString("iosFallbackUrl", mapIos));
        }
        if (mapIos.get(("ipadFallbackUrl")) != null) {
            builder.setIPadFallbackUrl(ValueGetter.getString("ipadFallbackUrl", mapIos));
        }
        if (mapIos.get(("ipadBundleId")) != null) {
            builder.setIPadBundleId(ValueGetter.getString("ipadBundleId", mapIos));
        }
        if (mapTunes != null) {
            final AppLinking.ITunesConnectCampaignInfo.Builder iTunesBuilder;
            iTunesBuilder = AppLinking.ITunesConnectCampaignInfo.newBuilder();
            if (mapTunes.get(("iTunesConnectProviderToken")) != null) {
                iTunesBuilder.setProviderToken(ValueGetter.getString("iTunesConnectProviderToken", mapTunes));
            }
            if (mapTunes.get(("iTunesConnectCampaignToken")) != null) {
                iTunesBuilder.setCampaignToken(ValueGetter.getString("iTunesConnectCampaignToken", mapTunes));
            }
            if (mapTunes.get(("iTunesConnectAffiliateToken")) != null) {
                iTunesBuilder.setAffiliateToken(ValueGetter.getString("iTunesConnectAffiliateToken", mapTunes));
            }
            if (mapTunes.get(("iTunesConnectMediaType")) != null) {
                iTunesBuilder.setMediaType(ValueGetter.getString("iTunesConnectMediaType", mapTunes));
            }
            builder.setITunesConnectCampaignInfo(iTunesBuilder.build());
        }
        return builder.build();
    }

    private AppLinking.Builder createAppLinkingWithInfo() {
        MethodCall call = replyHandler.getCall();
        final AppLinking.Builder builder = AppLinking.newBuilder();

        if (call.argument("deepLink") != null) {
            builder.setDeepLink(Uri.parse(call.argument("deepLink")));
            ;
        }
        if (call.argument("domainUriPrefix") != null) {
            builder.setUriPrefix(call.argument("domainUriPrefix"));
        }
        if (call.argument("longLink") != null) {
            builder.setLongLink(Uri.parse(call.argument("longLink")));
        }
        if (call.argument("socialCardInfo") != null) {
            builder.setSocialCardInfo(createSocialCardInfo(call.<Map<String, Object>>argument("socialCardInfo")));
        }
        if (call.argument("campaignInfo") != null) {
            builder.setCampaignInfo(createCampaignInfo(call.<Map<String, Object>>argument("campaignInfo")));
        }
        if (call.argument("androidLinkInfo") != null) {
            builder.setAndroidLinkInfo(createAndroidLinkInfo(call.<Map<String, Object>>argument("androidLinkInfo")));
        }
        if (call.argument("iosLinkInfo") != null) {
            builder.setIOSLinkInfo(createIOSLinkInfo(call.<Map<String, Object>>argument("iosLinkInfo"),
                    call.<Map<String, Object>>argument("iTunesLinkInfo")));
        }

        if (call.argument("expireMinute") != null) {
            builder.setExpireMinute(call.<Integer>argument("expireMinute"));
        }
        if (call.argument("linkingPreviewType") != null) {
            builder.setPreviewType(AppLinking.LinkingPreviewType.valueOf(call.argument("linkingPreviewType")));
        }
        return builder;
    }

    public void getAppLinking(Activity activity) {
        AGConnectAppLinking.getInstance().getAppLinking(activity).addOnSuccessListener(resolvedLinkData -> {
            try {
                replyHandler.success(Utils.fromResolvedLinkDataToMap(resolvedLinkData));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> {
            try {
                e.printStackTrace();
                Log.e("getAppLinking", "错误了${e.localizedMessage}");
                replyHandler.success(new HashMap<String, Object>());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
