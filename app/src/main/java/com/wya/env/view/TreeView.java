package com.wya.env.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.google.gson.Gson;
import com.wya.env.R;
import com.wya.env.bean.doodle.Doodle;
import com.wya.env.bean.doodle.DoodlePattern;
import com.wya.env.bean.tree.TreeData;
import com.wya.env.common.CommonValue;
import com.wya.env.util.ByteUtil;
import com.wya.utils.utils.LogUtil;
import com.wya.utils.utils.ScreenUtil;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @date: 2020/7/18 9:13
 * @author: Chunjiang Mao
 * @classname: LampView
 * @describe: 灯光界面
 */
public class TreeView extends View {


    private String data_str = "{\"Devname\":\"CN3CNT304\",\"Dimension\":\"3\",\"LampTotalNumber\":304,\"AspectRatio\":1.0656,\"AddrTab\":[{\"addr\":15,\"X\": 0.0000,\"Y\": 0.9692,\"Z\":0.5000},{\"addr\":31,\"X\": 0.0271,\"Y\": 0.9692,\"Z\":0.3376},{\"addr\":47,\"X\": 0.1054,\"Y\": 0.9692,\"Z\":0.1929},{\"addr\":63,\"X\": 0.2265,\"Y\": 0.9692,\"Z\":0.0814},{\"addr\":79,\"X\": 0.3773,\"Y\": 0.9692,\"Z\":0.0153},{\"addr\":95,\"X\": 0.5413,\"Y\": 0.9692,\"Z\":0.0017},{\"addr\":111,\"X\": 0.7008,\"Y\": 0.9692,\"Z\":0.0421},{\"addr\":127,\"X\": 0.8386,\"Y\": 0.9692,\"Z\":0.1321},{\"addr\":143,\"X\": 0.9397,\"Y\": 0.9692,\"Z\":0.2620},{\"addr\":159,\"X\": 0.9932,\"Y\": 0.9692,\"Z\":0.4177},{\"addr\":175,\"X\": 0.9932,\"Y\": 0.9692,\"Z\":0.5823},{\"addr\":191,\"X\": 0.9397,\"Y\": 0.9692,\"Z\":0.7380},{\"addr\":207,\"X\": 0.8386,\"Y\": 0.9692,\"Z\":0.8679},{\"addr\":223,\"X\": 0.7008,\"Y\": 0.9692,\"Z\":0.9579},{\"addr\":239,\"X\": 0.5413,\"Y\": 0.9692,\"Z\":0.9983},{\"addr\":255,\"X\": 0.3773,\"Y\": 0.9692,\"Z\":0.9847},{\"addr\":271,\"X\": 0.2265,\"Y\": 0.9692,\"Z\":0.9186},{\"addr\":287,\"X\": 0.1054,\"Y\": 0.9692,\"Z\":0.8071},{\"addr\":303,\"X\": 0.0271,\"Y\": 0.9692,\"Z\":0.6624},{\"addr\":14,\"X\": 0.0305,\"Y\": 0.9066,\"Z\":0.5000},{\"addr\":30,\"X\": 0.0559,\"Y\": 0.9066,\"Z\":0.3475},{\"addr\":46,\"X\": 0.1295,\"Y\": 0.9066,\"Z\":0.2116},{\"addr\":62,\"X\": 0.2432,\"Y\": 0.9066,\"Z\":0.1069},{\"addr\":78,\"X\": 0.3847,\"Y\": 0.9066,\"Z\":0.0448},{\"addr\":94,\"X\": 0.5388,\"Y\": 0.9066,\"Z\":0.0321},{\"addr\":110,\"X\": 0.6886,\"Y\": 0.9066,\"Z\":0.0700},{\"addr\":126,\"X\": 0.8180,\"Y\": 0.9066,\"Z\":0.1546},{\"addr\":142,\"X\": 0.9129,\"Y\": 0.9066,\"Z\":0.2765},{\"addr\":158,\"X\": 0.9631,\"Y\": 0.9066,\"Z\":0.4227},{\"addr\":174,\"X\": 0.9631,\"Y\": 0.9066,\"Z\":0.5773},{\"addr\":190,\"X\": 0.9129,\"Y\": 0.9066,\"Z\":0.7235},{\"addr\":206,\"X\": 0.8180,\"Y\": 0.9066,\"Z\":0.8454},{\"addr\":222,\"X\": 0.6886,\"Y\": 0.9066,\"Z\":0.9300},{\"addr\":238,\"X\": 0.5388,\"Y\": 0.9066,\"Z\":0.9679},{\"addr\":254,\"X\": 0.3847,\"Y\": 0.9066,\"Z\":0.9552},{\"addr\":270,\"X\": 0.2432,\"Y\": 0.9066,\"Z\":0.8931},{\"addr\":286,\"X\": 0.1295,\"Y\": 0.9066,\"Z\":0.7884},{\"addr\":302,\"X\": 0.0559,\"Y\": 0.9066,\"Z\":0.6525},{\"addr\":13,\"X\": 0.0609,\"Y\": 0.8441,\"Z\":0.5000},{\"addr\":29,\"X\": 0.0847,\"Y\": 0.8441,\"Z\":0.3574},{\"addr\":45,\"X\": 0.1535,\"Y\": 0.8441,\"Z\":0.2303},{\"addr\":61,\"X\": 0.2599,\"Y\": 0.8441,\"Z\":0.1324},{\"addr\":77,\"X\": 0.3922,\"Y\": 0.8441,\"Z\":0.0744},{\"addr\":93,\"X\": 0.5363,\"Y\": 0.8441,\"Z\":0.0624},{\"addr\":109,\"X\": 0.6764,\"Y\": 0.8441,\"Z\":0.0979},{\"addr\":125,\"X\": 0.7974,\"Y\": 0.8441,\"Z\":0.1770},{\"addr\":141,\"X\": 0.8861,\"Y\": 0.8441,\"Z\":0.2910},{\"addr\":157,\"X\": 0.9331,\"Y\": 0.8441,\"Z\":0.4277},{\"addr\":173,\"X\": 0.9331,\"Y\": 0.8441,\"Z\":0.5723},{\"addr\":189,\"X\": 0.8861,\"Y\": 0.8441,\"Z\":0.7090},{\"addr\":205,\"X\": 0.7974,\"Y\": 0.8441,\"Z\":0.8230},{\"addr\":221,\"X\": 0.6764,\"Y\": 0.8441,\"Z\":0.9021},{\"addr\":237,\"X\": 0.5363,\"Y\": 0.8441,\"Z\":0.9376},{\"addr\":253,\"X\": 0.3922,\"Y\": 0.8441,\"Z\":0.9256},{\"addr\":269,\"X\": 0.2599,\"Y\": 0.8441,\"Z\":0.8676},{\"addr\":285,\"X\": 0.1535,\"Y\": 0.8441,\"Z\":0.7697},{\"addr\":301,\"X\": 0.0847,\"Y\": 0.8441,\"Z\":0.6426},{\"addr\":12,\"X\": 0.0914,\"Y\": 0.7815,\"Z\":0.5000},{\"addr\":28,\"X\": 0.1135,\"Y\": 0.7815,\"Z\":0.3673},{\"addr\":44,\"X\": 0.1778,\"Y\": 0.7815,\"Z\":0.2490},{\"addr\":60,\"X\": 0.2765,\"Y\": 0.7815,\"Z\":0.1579},{\"addr\":76,\"X\": 0.3997,\"Y\": 0.7815,\"Z\":0.1039},{\"addr\":92,\"X\": 0.5337,\"Y\": 0.7815,\"Z\":0.0928},{\"addr\":108,\"X\": 0.6641,\"Y\": 0.7815,\"Z\":0.1258},{\"addr\":124,\"X\": 0.7767,\"Y\": 0.7815,\"Z\":0.1994},{\"addr\":140,\"X\": 0.8593,\"Y\": 0.7815,\"Z\":0.3055},{\"addr\":156,\"X\": 0.9030,\"Y\": 0.7815,\"Z\":0.4327},{\"addr\":172,\"X\": 0.9030,\"Y\": 0.7815,\"Z\":0.5673},{\"addr\":188,\"X\": 0.8593,\"Y\": 0.7815,\"Z\":0.6945},{\"addr\":204,\"X\": 0.7767,\"Y\": 0.7815,\"Z\":0.8006},{\"addr\":220,\"X\": 0.6641,\"Y\": 0.7815,\"Z\":0.8742},{\"addr\":236,\"X\": 0.5337,\"Y\": 0.7815,\"Z\":0.9072},{\"addr\":252,\"X\": 0.3997,\"Y\": 0.7815,\"Z\":0.8961},{\"addr\":268,\"X\": 0.2765,\"Y\": 0.7815,\"Z\":0.8421},{\"addr\":284,\"X\": 0.1778,\"Y\": 0.7815,\"Z\":0.7510},{\"addr\":300,\"X\": 0.1135,\"Y\": 0.7815,\"Z\":0.6327},{\"addr\":11,\"X\": 0.1219,\"Y\": 0.7190,\"Z\":0.5000},{\"addr\":27,\"X\": 0.1424,\"Y\": 0.7190,\"Z\":0.3772},{\"addr\":43,\"X\": 0.2016,\"Y\": 0.7190,\"Z\":0.2677},{\"addr\":59,\"X\": 0.2932,\"Y\": 0.7190,\"Z\":0.1834},{\"addr\":75,\"X\": 0.4072,\"Y\": 0.7190,\"Z\":0.1335},{\"addr\":91,\"X\": 0.5312,\"Y\": 0.7190,\"Z\":0.1232},{\"addr\":107,\"X\": 0.6519,\"Y\": 0.7190,\"Z\":0.1537},{\"addr\":123,\"X\": 0.7561,\"Y\": 0.7190,\"Z\":0.2218},{\"addr\":139,\"X\": 0.8326,\"Y\": 0.7190,\"Z\":0.3200},{\"addr\":155,\"X\": 0.8730,\"Y\": 0.7190,\"Z\":0.4378},{\"addr\":171,\"X\": 0.8730,\"Y\": 0.7190,\"Z\":0.5622},{\"addr\":187,\"X\": 0.8326,\"Y\": 0.7190,\"Z\":0.6800},{\"addr\":203,\"X\": 0.7561,\"Y\": 0.7190,\"Z\":0.7782},{\"addr\":219,\"X\": 0.6519,\"Y\": 0.7190,\"Z\":0.8463},{\"addr\":235,\"X\": 0.5312,\"Y\": 0.7190,\"Z\":0.8768},{\"addr\":251,\"X\": 0.4072,\"Y\": 0.7190,\"Z\":0.8666},{\"addr\":267,\"X\": 0.2932,\"Y\": 0.7190,\"Z\":0.8166},{\"addr\":283,\"X\": 0.2016,\"Y\": 0.7190,\"Z\":0.7323},{\"addr\":299,\"X\": 0.1424,\"Y\": 0.7190,\"Z\":0.6228},{\"addr\":10,\"X\": 0.1523,\"Y\": 0.6564,\"Z\":0.5000},{\"addr\":26,\"X\": 0.1712,\"Y\": 0.6564,\"Z\":0.3871},{\"addr\":42,\"X\": 0.2257,\"Y\": 0.6564,\"Z\":0.2865},{\"addr\":58,\"X\": 0.3098,\"Y\": 0.6564,\"Z\":0.2090},{\"addr\":74,\"X\": 0.4147,\"Y\": 0.6564,\"Z\":0.1630},{\"addr\":90,\"X\": 0.5287,\"Y\": 0.6564,\"Z\":0.1535},{\"addr\":106,\"X\": 0.6397,\"Y\": 0.6564,\"Z\":0.1816},{\"addr\":122,\"X\": 0.7355,\"Y\": 0.6564,\"Z\":0.2442},{\"addr\":138,\"X\": 0.8058,\"Y\": 0.6564,\"Z\":0.3345},{\"addr\":154,\"X\": 0.8429,\"Y\": 0.6564,\"Z\":0.4428},{\"addr\":170,\"X\": 0.8429,\"Y\": 0.6564,\"Z\":0.5572},{\"addr\":186,\"X\": 0.8058,\"Y\": 0.6564,\"Z\":0.6655},{\"addr\":202,\"X\": 0.7355,\"Y\": 0.6564,\"Z\":0.7558},{\"addr\":218,\"X\": 0.6397,\"Y\": 0.6564,\"Z\":0.8184},{\"addr\":234,\"X\": 0.5287,\"Y\": 0.6564,\"Z\":0.8465},{\"addr\":250,\"X\": 0.4147,\"Y\": 0.6564,\"Z\":0.8370},{\"addr\":266,\"X\": 0.3098,\"Y\": 0.6564,\"Z\":0.7910},{\"addr\":282,\"X\": 0.2257,\"Y\": 0.6564,\"Z\":0.7135},{\"addr\":298,\"X\": 0.1712,\"Y\": 0.6564,\"Z\":0.6129},{\"addr\":9,\"X\": 0.1828,\"Y\": 0.5938,\"Z\":0.5000},{\"addr\":25,\"X\": 0.2000,\"Y\": 0.5938,\"Z\":0.3970},{\"addr\":41,\"X\": 0.2497,\"Y\": 0.5938,\"Z\":0.3052},{\"addr\":57,\"X\": 0.3265,\"Y\": 0.5938,\"Z\":0.2345},{\"addr\":73,\"X\": 0.4221,\"Y\": 0.5938,\"Z\":0.1925},{\"addr\":89,\"X\": 0.5262,\"Y\": 0.5938,\"Z\":0.1839},{\"addr\":105,\"X\": 0.6274,\"Y\": 0.5938,\"Z\":0.2095},{\"addr\":121,\"X\": 0.7148,\"Y\": 0.5938,\"Z\":0.2666},{\"addr\":137,\"X\": 0.7790,\"Y\": 0.5938,\"Z\":0.3490},{\"addr\":153,\"X\": 0.8129,\"Y\": 0.5938,\"Z\":0.4478},{\"addr\":169,\"X\": 0.8129,\"Y\": 0.5938,\"Z\":0.5522},{\"addr\":185,\"X\": 0.7790,\"Y\": 0.5938,\"Z\":0.6510},{\"addr\":201,\"X\": 0.7148,\"Y\": 0.5938,\"Z\":0.7334},{\"addr\":217,\"X\": 0.6274,\"Y\": 0.5938,\"Z\":0.7905},{\"addr\":233,\"X\": 0.5262,\"Y\": 0.5938,\"Z\":0.8161},{\"addr\":249,\"X\": 0.4221,\"Y\": 0.5938,\"Z\":0.8075},{\"addr\":265,\"X\": 0.3265,\"Y\": 0.5938,\"Z\":0.6948},{\"addr\":281,\"X\": 0.2497,\"Y\": 0.5938,\"Z\":0.6030},{\"addr\":297,\"X\": 0.2000,\"Y\": 0.5938,\"Z\":0.6030},{\"addr\":8,\"X\": 0.2133,\"Y\": 0.5313,\"Z\":0.5000},{\"addr\":24,\"X\": 0.2288,\"Y\": 0.5313,\"Z\":0.4069},{\"addr\":40,\"X\": 0.2737,\"Y\": 0.5313,\"Z\":0.3239},{\"addr\":56,\"X\": 0.3432,\"Y\": 0.5313,\"Z\":0.2600},{\"addr\":72,\"X\": 0.4296,\"Y\": 0.5313,\"Z\":0.2221},{\"addr\":88,\"X\": 0.5237,\"Y\": 0.5313,\"Z\":0.2143},{\"addr\":104,\"X\": 0.6152,\"Y\": 0.5313,\"Z\":0.2374},{\"addr\":120,\"X\": 0.6942,\"Y\": 0.5313,\"Z\":0.2891},{\"addr\":136,\"X\": 0.7522,\"Y\": 0.5313,\"Z\":0.3635},{\"addr\":152,\"X\": 0.7828,\"Y\": 0.5313,\"Z\":0.4528},{\"addr\":168,\"X\": 0.7828,\"Y\": 0.5313,\"Z\":0.5472},{\"addr\":184,\"X\": 0.7522,\"Y\": 0.5313,\"Z\":0.6365},{\"addr\":200,\"X\": 0.6942,\"Y\": 0.5313,\"Z\":0.7109},{\"addr\":216,\"X\": 0.6152,\"Y\": 0.5313,\"Z\":0.7626},{\"addr\":232,\"X\": 0.5237,\"Y\": 0.5313,\"Z\":0.7857},{\"addr\":248,\"X\": 0.4296,\"Y\": 0.5313,\"Z\":0.7779},{\"addr\":264,\"X\": 0.3432,\"Y\": 0.5313,\"Z\":0.7400},{\"addr\":280,\"X\": 0.2737,\"Y\": 0.5313,\"Z\":0.6761},{\"addr\":296,\"X\": 0.2288,\"Y\": 0.5313,\"Z\":0.5931},{\"addr\":7,\"X\": 0.2438,\"Y\": 0.4687,\"Z\":0.5000},{\"addr\":23,\"X\": 0.2576,\"Y\": 0.4687,\"Z\":0.4168},{\"addr\":39,\"X\": 0.2978,\"Y\": 0.4687,\"Z\":0.3426},{\"addr\":55,\"X\": 0.3598,\"Y\": 0.4687,\"Z\":0.2855},{\"addr\":71,\"X\": 0.4371,\"Y\": 0.4687,\"Z\":0.2516},{\"addr\":87,\"X\": 0.5212,\"Y\": 0.4687,\"Z\":0.2446},{\"addr\":103,\"X\": 0.6029,\"Y\": 0.4687,\"Z\":0.2653},{\"addr\":119,\"X\": 0.6735,\"Y\": 0.4687,\"Z\":0.3115},{\"addr\":135,\"X\": 0.7254,\"Y\": 0.4687,\"Z\":0.3780},{\"addr\":151,\"X\": 0.7528,\"Y\": 0.4687,\"Z\":0.4578},{\"addr\":167,\"X\": 0.7528,\"Y\": 0.4687,\"Z\":0.5422},{\"addr\":183,\"X\": 0.7254,\"Y\": 0.4687,\"Z\":0.6220},{\"addr\":199,\"X\": 0.6735,\"Y\": 0.4687,\"Z\":0.6885},{\"addr\":215,\"X\": 0.6029,\"Y\": 0.4687,\"Z\":0.7347},{\"addr\":231,\"X\": 0.5212,\"Y\": 0.4687,\"Z\":0.7554},{\"addr\":247,\"X\": 0.4371,\"Y\": 0.4687,\"Z\":0.7484},{\"addr\":263,\"X\": 0.3598,\"Y\": 0.4687,\"Z\":0.7145},{\"addr\":279,\"X\": 0.2978,\"Y\": 0.4687,\"Z\":0.6574},{\"addr\":295,\"X\": 0.2576,\"Y\": 0.4687,\"Z\":0.5832},{\"addr\":6,\"X\": 0.2742,\"Y\": 0.4062,\"Z\":0.5000},{\"addr\":22,\"X\": 0.2865,\"Y\": 0.4062,\"Z\":0.4267},{\"addr\":38,\"X\": 0.3218,\"Y\": 0.4062,\"Z\":0.3613},{\"addr\":54,\"X\": 0.3765,\"Y\": 0.4062,\"Z\":0.3110},{\"addr\":70,\"X\": 0.4446,\"Y\": 0.4062,\"Z\":0.2811},{\"addr\":86,\"X\": 0.5186,\"Y\": 0.4062,\"Z\":0.2750},{\"addr\":102,\"X\": 0.5907,\"Y\": 0.4062,\"Z\":0.2932},{\"addr\":118,\"X\": 0.6529,\"Y\": 0.4062,\"Z\":0.3339},{\"addr\":134,\"X\": 0.6986,\"Y\": 0.4062,\"Z\":0.3925},{\"addr\":150,\"X\": 0.7227,\"Y\": 0.4062,\"Z\":0.4628},{\"addr\":166,\"X\": 0.7227,\"Y\": 0.4062,\"Z\":0.5372},{\"addr\":182,\"X\": 0.6986,\"Y\": 0.4062,\"Z\":0.6075},{\"addr\":198,\"X\": 0.6529,\"Y\": 0.4062,\"Z\":0.6661},{\"addr\":214,\"X\": 0.5907,\"Y\": 0.4062,\"Z\":0.7068},{\"addr\":230,\"X\": 0.5186,\"Y\": 0.4062,\"Z\":0.7250},{\"addr\":246,\"X\": 0.4446,\"Y\": 0.4062,\"Z\":0.7189},{\"addr\":262,\"X\": 0.3765,\"Y\": 0.4062,\"Z\":0.6890},{\"addr\":278,\"X\": 0.3218,\"Y\": 0.4062,\"Z\":0.6387},{\"addr\":294,\"X\": 0.2865,\"Y\": 0.4062,\"Z\":0.5733},{\"addr\":5,\"X\": 0.3047,\"Y\": 0.3436,\"Z\":0.5000},{\"addr\":21,\"X\": 0.3153,\"Y\": 0.3436,\"Z\":0.4366},{\"addr\":37,\"X\": 0.3459,\"Y\": 0.3436,\"Z\":0.3800},{\"addr\":53,\"X\": 0.3932,\"Y\": 0.3436,\"Z\":0.3365},{\"addr\":69,\"X\": 0.4521,\"Y\": 0.3436,\"Z\":0.3107},{\"addr\":85,\"X\": 0.5161,\"Y\": 0.3436,\"Z\":0.3054},{\"addr\":101,\"X\": 0.5785,\"Y\": 0.3436,\"Z\":0.3211},{\"addr\":117,\"X\": 0.6323,\"Y\": 0.3436,\"Z\":0.3563},{\"addr\":133,\"X\": 0.6718,\"Y\": 0.3436,\"Z\":0.4070},{\"addr\":149,\"X\": 0.6926,\"Y\": 0.3436,\"Z\":0.4679},{\"addr\":165,\"X\": 0.6926,\"Y\": 0.3436,\"Z\":0.5321},{\"addr\":181,\"X\": 0.6718,\"Y\": 0.3436,\"Z\":0.5930},{\"addr\":197,\"X\": 0.6323,\"Y\": 0.3436,\"Z\":0.6437},{\"addr\":213,\"X\": 0.5785,\"Y\": 0.3436,\"Z\":0.6789},{\"addr\":229,\"X\": 0.5161,\"Y\": 0.3436,\"Z\":0.6946},{\"addr\":245,\"X\": 0.4521,\"Y\": 0.3436,\"Z\":0.6893},{\"addr\":261,\"X\": 0.3932,\"Y\": 0.3436,\"Z\":0.6635},{\"addr\":277,\"X\": 0.3459,\"Y\": 0.3436,\"Z\":0.6200},{\"addr\":293,\"X\": 0.3153,\"Y\": 0.3436,\"Z\":0.5634},{\"addr\":4,\"X\": 0.3352,\"Y\": 0.2810,\"Z\":0.5000},{\"addr\":20,\"X\": 0.3441,\"Y\": 0.2810,\"Z\":0.4465},{\"addr\":36,\"X\": 0.3699,\"Y\": 0.2810,\"Z\":0.3988},{\"addr\":52,\"X\": 0.4098,\"Y\": 0.2810,\"Z\":0.3620},{\"addr\":68,\"X\": 0.4595,\"Y\": 0.2810,\"Z\":0.3402},{\"addr\":84,\"X\": 0.5136,\"Y\": 0.2810,\"Z\":0.3357},{\"addr\":100,\"X\": 0.5662,\"Y\": 0.2810,\"Z\":0.3490},{\"addr\":116,\"X\": 0.6116,\"Y\": 0.2810,\"Z\":0.3787},{\"addr\":132,\"X\": 0.6450,\"Y\": 0.2810,\"Z\":0.4215},{\"addr\":148,\"X\": 0.6626,\"Y\": 0.2810,\"Z\":0.4729},{\"addr\":164,\"X\": 0.6626,\"Y\": 0.2810,\"Z\":0.5271},{\"addr\":180,\"X\": 0.6450,\"Y\": 0.2810,\"Z\":0.5785},{\"addr\":196,\"X\": 0.6116,\"Y\": 0.2810,\"Z\":0.6213},{\"addr\":212,\"X\": 0.5662,\"Y\": 0.2810,\"Z\":0.6510},{\"addr\":228,\"X\": 0.5136,\"Y\": 0.2810,\"Z\":0.6643},{\"addr\":244,\"X\": 0.4595,\"Y\": 0.2810,\"Z\":0.6598},{\"addr\":260,\"X\": 0.4098,\"Y\": 0.2810,\"Z\":0.6380},{\"addr\":276,\"X\": 0.3699,\"Y\": 0.2810,\"Z\":0.6012},{\"addr\":292,\"X\": 0.3441,\"Y\": 0.2810,\"Z\":0.5535},{\"addr\":3,\"X\": 0.3656,\"Y\": 0.2185,\"Z\":0.5000},{\"addr\":19,\"X\": 0.3729,\"Y\": 0.2185,\"Z\":0.4564},{\"addr\":35,\"X\": 0.3940,\"Y\": 0.2185,\"Z\":0.4175},{\"addr\":51,\"X\": 0.4265,\"Y\": 0.2185,\"Z\":0.3875},{\"addr\":67,\"X\": 0.4670,\"Y\": 0.2185,\"Z\":0.3697},{\"addr\":83,\"X\": 0.5111,\"Y\": 0.2185,\"Z\":0.3661},{\"addr\":99,\"X\": 0.5540,\"Y\": 0.2185,\"Z\":0.3769},{\"addr\":115,\"X\": 0.5910,\"Y\": 0.2185,\"Z\":0.4011},{\"addr\":131,\"X\": 0.6182,\"Y\": 0.2185,\"Z\":0.4360},{\"addr\":147,\"X\": 0.6325,\"Y\": 0.2185,\"Z\":0.4779},{\"addr\":163,\"X\": 0.6325,\"Y\": 0.2185,\"Z\":0.5221},{\"addr\":179,\"X\": 0.6182,\"Y\": 0.2185,\"Z\":0.5640},{\"addr\":195,\"X\": 0.5910,\"Y\": 0.2185,\"Z\":0.5989},{\"addr\":211,\"X\": 0.5540,\"Y\": 0.2185,\"Z\":0.6231},{\"addr\":227,\"X\": 0.5111,\"Y\": 0.2185,\"Z\":0.6339},{\"addr\":243,\"X\": 0.4670,\"Y\": 0.2185,\"Z\":0.6303},{\"addr\":259,\"X\": 0.4265,\"Y\": 0.2185,\"Z\":0.6125},{\"addr\":275,\"X\": 0.3940,\"Y\": 0.2185,\"Z\":0.5825},{\"addr\":291,\"X\": 0.3729,\"Y\": 0.2185,\"Z\":0.5436},{\"addr\":2,\"X\": 0.3961,\"Y\": 0.1559,\"Z\":0.5000},{\"addr\":18,\"X\": 0.4017,\"Y\": 0.1559,\"Z\":0.4663},{\"addr\":34,\"X\": 0.4180,\"Y\": 0.1559,\"Z\":0.4362},{\"addr\":50,\"X\": 0.4432,\"Y\": 0.1559,\"Z\":0.4130},{\"addr\":66,\"X\": 0.4745,\"Y\": 0.1559,\"Z\":0.3993},{\"addr\":82,\"X\": 0.5086,\"Y\": 0.1559,\"Z\":0.3965},{\"addr\":98,\"X\": 0.5417,\"Y\": 0.1559,\"Z\":0.4049},{\"addr\":114,\"X\": 0.5704,\"Y\": 0.1559,\"Z\":0.4236},{\"addr\":130,\"X\": 0.5914,\"Y\": 0.1559,\"Z\":0.4505},{\"addr\":146,\"X\": 0.6025,\"Y\": 0.1559,\"Z\":0.4829},{\"addr\":162,\"X\": 0.6025,\"Y\": 0.1559,\"Z\":0.5171},{\"addr\":178,\"X\": 0.5914,\"Y\": 0.1559,\"Z\":0.5495},{\"addr\":194,\"X\": 0.5704,\"Y\": 0.1559,\"Z\":0.5764},{\"addr\":210,\"X\": 0.5417,\"Y\": 0.1559,\"Z\":0.5951},{\"addr\":226,\"X\": 0.5086,\"Y\": 0.1559,\"Z\":0.6035},{\"addr\":242,\"X\": 0.4745,\"Y\": 0.1559,\"Z\":0.6007},{\"addr\":258,\"X\": 0.4432,\"Y\": 0.1559,\"Z\":0.5870},{\"addr\":274,\"X\": 0.4180,\"Y\": 0.1559,\"Z\":0.5638},{\"addr\":290,\"X\": 0.4017,\"Y\": 0.1559,\"Z\":0.5337},{\"addr\":1,\"X\": 0.4266,\"Y\": 0.0934,\"Z\":0.5000},{\"addr\":17,\"X\": 0.4306,\"Y\": 0.0934,\"Z\":0.4762},{\"addr\":33,\"X\": 0.4421,\"Y\": 0.0934,\"Z\":0.4549},{\"addr\":49,\"X\": 0.4598,\"Y\": 0.0934,\"Z\":0.4385},{\"addr\":65,\"X\": 0.4820,\"Y\": 0.0934,\"Z\":0.4288},{\"addr\":81,\"X\": 0.5061,\"Y\": 0.0934,\"Z\":0.4268},{\"addr\":97,\"X\": 0.5295,\"Y\": 0.0934,\"Z\":0.4328},{\"addr\":113,\"X\": 0.5497,\"Y\": 0.0934,\"Z\":0.4460},{\"addr\":129,\"X\": 0.5646,\"Y\": 0.0934,\"Z\":0.4651},{\"addr\":145,\"X\": 0.5724,\"Y\": 0.0934,\"Z\":0.4879},{\"addr\":161,\"X\": 0.5724,\"Y\": 0.0934,\"Z\":0.5121},{\"addr\":177,\"X\": 0.5646,\"Y\": 0.0934,\"Z\":0.5349},{\"addr\":193,\"X\": 0.5497,\"Y\": 0.0934,\"Z\":0.5540},{\"addr\":209,\"X\": 0.5295,\"Y\": 0.0934,\"Z\":0.5672},{\"addr\":225,\"X\": 0.5061,\"Y\": 0.0934,\"Z\":0.5732},{\"addr\":241,\"X\": 0.4820,\"Y\": 0.0934,\"Z\":0.5712},{\"addr\":257,\"X\": 0.4598,\"Y\": 0.0934,\"Z\":0.5615},{\"addr\":273,\"X\": 0.4421,\"Y\": 0.0934,\"Z\":0.5451},{\"addr\":289,\"X\": 0.4306,\"Y\": 0.0934,\"Z\":0.5238},{\"addr\":0,\"X\": 0.4570,\"Y\": 0.0308,\"Z\":0.5000},{\"addr\":16,\"X\": 0.4594,\"Y\": 0.0308,\"Z\":0.4861},{\"addr\":32,\"X\": 0.4661,\"Y\": 0.0308,\"Z\":0.4736},{\"addr\":48,\"X\": 0.4765,\"Y\": 0.0308,\"Z\":0.4640},{\"addr\":64,\"X\": 0.4895,\"Y\": 0.0308,\"Z\":0.4584},{\"addr\":80,\"X\": 0.5035,\"Y\": 0.0308,\"Z\":0.4572},{\"addr\":96,\"X\": 0.5173,\"Y\": 0.0308,\"Z\":0.4607},{\"addr\":112,\"X\": 0.5291,\"Y\": 0.0308,\"Z\":0.4684},{\"addr\":128,\"X\": 0.5378,\"Y\": 0.0308,\"Z\":0.4796},{\"addr\":144,\"X\": 0.5424,\"Y\": 0.0308,\"Z\":0.4929},{\"addr\":160,\"X\": 0.5424,\"Y\": 0.0308,\"Z\":0.5071},{\"addr\":176,\"X\": 0.5378,\"Y\": 0.0308,\"Z\":0.5204},{\"addr\":192,\"X\": 0.5291,\"Y\": 0.0308,\"Z\":0.5316},{\"addr\":208,\"X\": 0.5173,\"Y\": 0.0308,\"Z\":0.5393},{\"addr\":224,\"X\": 0.5035,\"Y\": 0.0308,\"Z\":0.5428},{\"addr\":240,\"X\": 0.4895,\"Y\": 0.0308,\"Z\":0.5417},{\"addr\":256,\"X\": 0.4765,\"Y\": 0.0308,\"Z\":0.5360},{\"addr\":272,\"X\": 0.4661,\"Y\": 0.0308,\"Z\":0.5264},{\"addr\":288,\"X\": 0.4594,\"Y\": 0.0308,\"Z\":0.5139}]}";

    /**
     * 背景画笔
     */
    private Paint bgPaint;
    /**
     * 灯
     */
    private Paint lampPaint;

    /**
     * 边框
     */
    private Paint framePaint;
    private Paint whitePaint;

    private Context mContext;

    private int isMirror;

    private boolean isStopSendUdpData;
    private boolean isStopSendUdpModeData;
    private boolean toClean;


    public TreeView(Context context) {
        super(context);
    }

    public TreeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TreeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttr(attrs);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TreeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 背景画笔
        bgPaint = new Paint();
        // 消除锯齿
        bgPaint.setAntiAlias(true);
        //防抖动
        bgPaint.setDither(true);
        bgPaint.setColor(mBackground);


        // 画背景
        canvas.drawRect(0, 0, mWidth, mHeight, bgPaint);

        // 画灯
        lampPaint = new Paint();
        // 消除锯齿
        lampPaint.setAntiAlias(true);
        // 防抖动
        lampPaint.setDither(true);

        // 画边框
        framePaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        // 消除锯齿
        framePaint.setAntiAlias(true);
        framePaint.setStrokeWidth(2);
        framePaint.setStyle(Paint.Style.STROKE);        // 防抖动
        framePaint.setDither(true);
        framePaint.setColor(Color.parseColor("#5662EA"));

//        // 画边框
//        whitePaint = new Paint();
//        // 消除锯齿
//        whitePaint.setAntiAlias(true);
//        // 防抖动
//        whitePaint.setDither(true);
//        whitePaint.setColor(getResources().getColor(android.R.color.transparent));

        for (int i = 0; i < data.size(); i++) {
            if (data.get(String.valueOf(i)) != null && data.get(String.valueOf(i)).getColor() != null && !data.get(String.valueOf(i)).getColor().equals("#000000")) {
                if (data.get(String.valueOf(i)).isFlash() == 1) {
//                        data.get(String.valueOf(j + size / column * i)).setLight(light);
                    lampPaint.setColor(data.get(String.valueOf(i)).getLampColor(light * 255 / 100));
                } else {
                    lampPaint.setColor(data.get(String.valueOf(i)).getLampColor(255));
                }
            } else {
                lampPaint.setColor(Color.argb(0, 0, 0, 0));
            }
            canvas.drawCircle(data.get(String.valueOf(i)).getX() * (mWidth - lamp_size - 2) + lamp_size / 2 + 1, data.get(String.valueOf(i)).getY() * (mHeight - lamp_size - 2) + lamp_size / 2 + 1, lamp_size / 2, framePaint);
//            canvas.drawCircle(data.get(String.valueOf(i)).getX() * (mWidth - lamp_size) + lamp_size / 2, data.get(String.valueOf(i)).getY() * mHeight, lamp_size / 2, whitePaint);
            canvas.drawCircle(data.get(String.valueOf(i)).getX() * (mWidth - lamp_size) + lamp_size / 2, data.get(String.valueOf(i)).getY() * (mHeight - lamp_size) + lamp_size / 2, lamp_size / 2, lampPaint);
        }
    }


    /**
     * 背景宽度
     */
    private int mWidth;

    /**
     * 背景高度
     */
    private int mHeight;
    /**
     * 背景颜色
     */
    private int mBackground;

    /**
     * 灯光颜色
     */
    private int mLampColor;


    /**
     * 灯的个数
     */
    private int size;

    /**
     * 灯的直径
     */
    private int lamp_size;
    /**
     * 灯离边上的距离
     */
    private int lamp_margin;
    /**
     * 灯的列数
     */
    private int column;

    /**
     * 选择的颜色
     */
    private String choseColor;

    /**
     * 展示的颜色
     */
    private String showColor;

//    /**
//     * 亮度
//     */
//    private int choseLight;


    private int light;

    private boolean isPaintBold;

    private int type;

    /**
     * 是否闪烁
     */
    private boolean isTwinkle;

    /**
     * 是否存在闪烁
     */
    private boolean hasTwinkle;

    /**
     * 闪烁一次时间  ms
     */
    private int period;

    /**
     * 每一帧时间
     */
    private int frameTime;

    /**
     * 模板每一帧时间
     */
    private int modelFrameTime;

    /**
     * 模板每一帧时间
     */
    private int sendDataTime;

    /**
     * 模板名字
     */
    private String modelName;

    /**
     * 实时画板
     */
    private boolean isOnline;

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * @return 灯的数量
     */
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
        // 灯的直径
        lamp_size = mWidth / column - 2 * lamp_margin;
        mHeight = (size / column) * (lamp_size + 2 * lamp_margin);
    }

    public void setTwinkle(boolean twinkle) {
        isTwinkle = twinkle;
        if (hasTwinkle() || isTwinkle) {
            toTwinkle();
        }
    }

    private boolean hasTwinkle() {
        hasTwinkle = false;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(String.valueOf(i)).isFlash() == 1) {
                hasTwinkle = true;
                break;
            }
        }
        return hasTwinkle;
    }


    /**
     * 闪烁线程开启
     */
    private int add;
    private ScheduledExecutorService twinkleExecutorService;
    private Runnable twinkleTask;
    private boolean isAdd = false;

    private void toTwinkle() {
        stopTwinkle();
        add = 0;
        if (twinkleExecutorService == null) {
            LogUtil.e("启动闪烁");
            twinkleExecutorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("twinkleExecutorService").daemon(true).build());
        }
        if (twinkleTask == null) {
            twinkleTask = new Runnable() {
                @Override
                public void run() {
                    if (add != -1) {
                        if (light >= 255) {
                            isAdd = false;
                        } else if (light <= 0) {
                            isAdd = true;
                        }
                        if (isAdd) {
                            if (light > 250) {
                                light = 255;
                            } else {
                                light = light + 5;
                            }
                        } else {
                            if (light < 5) {
                                light = 0;
                            } else {
                                light = light - 5;
                            }
                        }
                        LogUtil.e("light:" + light);
                        add = 0;
                        postInvalidate();
                    }
                }
            };
        }
        if (twinkleExecutorService != null) {
            twinkleExecutorService.scheduleAtFixedRate(twinkleTask, 0, frameTime / 10, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 开始闪烁
     */
    public void startTwinkle() {
        if (isOnline) {
            if (hasTwinkle()) {
                LogUtil.e("启动闪烁");
                toTwinkle();
            }
        }
    }

    /**
     * 停止闪烁
     */
    public void stopTwinkle() {
        LogUtil.e("停止闪烁");
        if (twinkleExecutorService != null) {
            LogUtil.e("停止闪烁 twinkleExecutorService != null");
            twinkleExecutorService.shutdownNow();
        }
        // 非单例模式，置空防止重复的任务
        twinkleExecutorService = null;
    }

    public void setPaintBold(boolean paintBold) {
        isPaintBold = paintBold;
    }

    public String getChoseColor() {
        return choseColor;
    }

    public void setChoseColor(String choseColor) {
        if (choseColor == null) {
            choseColor = "#000000";
        }
        this.choseColor = choseColor;
    }

    public void setShowColor(String showColor) {
        if (showColor == null) {
            showColor = "#000000";
        }
        this.showColor = showColor;
    }

    private HashMap<String, Doodle> data = new HashMap<>();
    private HashMap<String, Doodle> clean_data = new HashMap<>();


    public void setModel(List<DoodlePattern> modeArr, int light, boolean toShow) {
        LogUtil.e("toShow=============" + toShow);
        this.size = modeArr.get(0).getSize();
        this.light = light;
        mHeight = (size / column) * (lamp_size + 2 * lamp_margin);
        setMeasuredDimension(mWidth, mHeight);
        if (modeArr.size() == 1) {
            addMode = -1;
            this.data = modeArr.get(0).getLight_status();
            if (hasTwinkle()) {
                toTwinkle();
                if (toShow) {
                    isStopSendUdpData = false;
                    sendUdpMessage();
                    stopSendUdpModeData();
                }
            } else {
                stopTwinkle();
                postInvalidate();
                if (toShow) {
                    isStopSendUdpData = false;
                    sendUdpMessage();
                    stopSendUdpModeData();
                }
            }
        } else {
            add = -1;
            this.modeArr = modeArr;
            toShowModel(toShow);
            stopSendUdpData();
        }
    }

    private int addMode;
    ScheduledExecutorService modelExecutorService;
    private List<DoodlePattern> modeArr;

    private void toShowModel(boolean toShow) {
        addMode = 0;
        isStopSendUdpModeData = false;
        if (modelExecutorService == null) {
            modelExecutorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("toShowModel").daemon(true).build());
            modelExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (addMode != -1) {
                        data = modeArr.get(addMode % modeArr.size()).getLight_status();
                        addMode++;
                        postInvalidate();
                        if (toShow) {
                            try {
                                if (isStopSendUdpModeData) {
                                    if (toClean) {
                                        send("255.255.255.255", CommonValue.UDP_PORT, getUdpByteData(cleanData(data)), "模板");
                                        LogUtil.e("清除灯数据成功");
                                    }
                                    stopSendUdpModeData();
                                } else {
                                    send("255.255.255.255", CommonValue.UDP_PORT, getUdpByteData(isMirror == 1 ? toMirror(modeArr.get(addMode % modeArr.size()).getLight_status()) : modeArr.get(addMode % modeArr.size()).getLight_status()), "模板");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                LogUtil.e("发送UDP数据失败");
                            }
                        }
                    }
                }
            }, 0, modelFrameTime, TimeUnit.MILLISECONDS);
        }
    }

    private HashMap<String, Doodle> cleanData(HashMap<String, Doodle> data) {
        clean_data.clear();
        for (int i = 0; i < data.size(); i++) {
            Doodle doodle = new Doodle();
            doodle.setColor("#000000");
            doodle.setFlash(0);
            clean_data.put(String.valueOf(i), doodle);
        }
        return clean_data;
    }


    private HashMap<String, Doodle> save_data = new HashMap<>();

    public HashMap<String, Doodle> getSaveData() {
        save_data.clear();
        for (int i = 0; i < data.size(); i++) {
            if (!data.get(String.valueOf(i)).getColor().equals("#000000")) {
                save_data.put(String.valueOf(i), data.get(String.valueOf(i)));
            }
        }
        return save_data;
    }


    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.LampView);
        mWidth = (int) typedArray.getDimension(R.styleable.LampView_width, ScreenUtil.getScreenWidth(mContext) - (int) typedArray.getDimension(R.styleable.LampView_margin_left, 0) - (int) typedArray.getDimension(R.styleable.LampView_margin_right, 0));
        mHeight = mWidth;
        lamp_size = 30;
//
//        type = typedArray.getInt(R.styleable.LampView_type, 1);
        isOnline = typedArray.getBoolean(R.styleable.LampView_online, false);
//
//
//
//        if (type == 1) {
//              } else if (type == 2) {
//            mWidth = (int) typedArray.getDimension(R.styleable.LampView_width, ScreenUtil.getScreenWidth(mContext) / 2 - (int) typedArray.getDimension(R.styleable.LampView_margin_left, 0) - (int) typedArray.getDimension(R.styleable.LampView_margin_right, 0) - ScreenUtil.dip2px(mContext, 20));
//        }
//
////        column = typedArray.getColor(R.styleable.LampView_column, 15);
//        //TODO
//        column = 15;
//        if (type == 1) {
//            lamp_margin = typedArray.getColor(R.styleable.LampView_lamp_margin, 2);
//        } else if (type == 2) {
//            lamp_margin = typedArray.getColor(R.styleable.LampView_lamp_margin, 1);
//        }
//
//        size = typedArray.getColor(R.styleable.LampView_size, 600);
//
//        // 灯的直径
//
//
//        mHeight = (size / column) * (lamp_size + 2 * lamp_margin);

        mBackground = typedArray.getColor(R.styleable.LampView_bg_color,
                mContext.getResources().getColor(R.color.c999999));
        mLampColor = typedArray.getColor(R.styleable.LampView_lamp_color,
                mContext.getResources().getColor(R.color.black));

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }


    private TreeData treeData;
    private List<Doodle> treeDoodles;

    public void init() {
        treeData = new Gson().fromJson(data_str, TreeData.class);
        treeDoodles = treeData.getAddrTab();


        choseColor = "#000000";
        showColor = "#000000";
        light = 255;

        hasTwinkle = false;
        period = 2000;
        frameTime = 200;
        modelFrameTime = 200;
        sendDataTime = 200;


        data.clear();
        for (int i = 0; i < treeDoodles.size(); i++) {
            Doodle doodle = new Doodle();
            doodle.setColor("#5662EA");
            doodle.setFlash(0);
            doodle.setAddr(treeDoodles.get(i).getAddr());
            doodle.setX(treeDoodles.get(i).getX());
            doodle.setY(treeDoodles.get(i).getY());
            doodle.setZ(treeDoodles.get(i).getZ());
            data.put(String.valueOf(treeDoodles.get(i).getAddr()), doodle);
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    float old_x = 0;
    float old_y = 0;
    int x;
    int y;
    long createTime;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isOnline) {
            return super.onTouchEvent(event);
        } else {
            ViewParent parent = getParent();
            if (parent != null) {
                //父控件不拦截事件，全部交给子控件处理
                parent.requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        createTime = System.currentTimeMillis();
                        x = (int) event.getX();
                        y = (int) event.getY();
                        old_x = event.getX();
                        old_y = event.getY();
                        setColor(old_x, old_y);


//                        if (x > 0 && x < column * (lamp_size + 2 * lamp_margin) && y > 0 && y < (size / column) * (lamp_size + 2 * lamp_margin)) {
//                            old_x = event.getX();
//                            old_y = event.getY();
//                            int position = ((int) ((event.getX()) / (lamp_size + 2 * lamp_margin)) * (size / column) + ((int) (event.getY() / (lamp_size + 2 * lamp_margin))));
//                            LogUtil.e("i----" + (int) ((event.getX()) / (lamp_size + 2 * lamp_margin)) + "j----" + ((int) (event.getY() / (lamp_size + 2 * lamp_margin))));
//                            LogUtil.e("postion----" + position);
//                            if (isPaintBold) {
//                                setBoldAllChoseColor(position);
//                            } else {
//                                if (!data.get(String.valueOf(position)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position)).isFlash() == 1) != isTwinkle) {
//                                    data.get(String.valueOf(position)).setColor(choseColor);
//                                    data.get(String.valueOf(position)).setShowColor(showColor);
////                                    data.get(String.valueOf(position)).setLight(choseLight);
//                                    if (isTwinkle && choseColor != "#000000") {
//                                        data.get(String.valueOf(position)).setFlash(1);
//                                    } else {
//                                        data.get(String.valueOf(position)).setFlash(0);
//                                    }
//                                    postInvalidate();
//                                    sendUdpDataAdd = 0;
//                                }
//                            }
//                            if (!hasTwinkle) {
//                                startTwinkle();
//                            }
//                        } else {
//                            LogUtil.e("在外面");
//                        }
                    case MotionEvent.ACTION_MOVE:
                        x = (int) event.getX();
                        y = (int) event.getY();
                        old_x = event.getX();
                        old_y = event.getY();
                        setColor(old_x, old_y);
//                        if (x > 0 && x < column * (lamp_size + 2 * lamp_margin) && y > 0 && y < (size / column) * (lamp_size + 2 * lamp_margin)) {
//                            if (old_x == 0 || old_y == 0 || Math.abs(old_x - event.getX()) > lamp_size + 2 * lamp_margin || Math.abs(old_y - event.getY()) > lamp_size + 2 * lamp_margin) {
//                                old_x = event.getX();
//                                old_y = event.getY();
//                                int position = ((int) ((event.getX()) / (lamp_size + 2 * lamp_margin)) * (size / column) + ((int) (event.getY() / (lamp_size + 2 * lamp_margin))));
//                                LogUtil.e("i----" + (int) ((event.getX()) / (lamp_size + 2 * lamp_margin)) + "j----" + ((int) (event.getY() / (lamp_size + 2 * lamp_margin))));
//                                LogUtil.e("postion----" + position);
//                                if (isPaintBold) {
//                                    setBoldAllChoseColor(position);
//                                } else {
//                                    if (!data.get(String.valueOf(position)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position)).isFlash() == 1) != isTwinkle) {
//                                        data.get(String.valueOf(position)).setColor(choseColor);
//                                        data.get(String.valueOf(position)).setShowColor(showColor);
////                                        data.get(String.valueOf(position)).setLight(choseLight);
//                                        if (isTwinkle && choseColor != "#000000") {
//                                            data.get(String.valueOf(position)).setFlash(1);
//                                        } else {
//                                            data.get(String.valueOf(position)).setFlash(0);
//                                        }
//                                        postInvalidate();
//                                        sendUdpDataAdd = 0;
//                                    }
//                                }
//                            }
//                        } else {
//                            LogUtil.e("在外面");
//                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;

                    default:
                        break;
                }
            }
            return true;
        }

    }

    private void setColor(float old_x, float old_y) {
        for (int i = 0; i < data.size(); i++) {
            if (Math.abs(old_x - data.get(String.valueOf(i)).getX() * (mWidth - lamp_size)) < lamp_size && Math.abs(old_y - data.get(String.valueOf(i)).getY() * mHeight) < lamp_size) {
//                LogUtil.e(old_x + "-----" + old_y + "---" + data.get(String.valueOf(i)).getX() * (mWidth - lamp_size) + "---" + data.get(String.valueOf(i)).getY() * mHeight);
//                LogUtil.e(data.get(String.valueOf(i)).getColor() + "------------" + choseColor);
                if (!data.get(String.valueOf(i)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(i)).isFlash() == 1) != isTwinkle) {
                    data.get(String.valueOf(i)).setColor(choseColor);
                    data.get(String.valueOf(i)).setShowColor(showColor);
//                                        data.get(String.valueOf(position)).setLight(choseLight);
                    if (isTwinkle && choseColor != "#000000") {
                        data.get(String.valueOf(i)).setFlash(1);
                    } else {
                        data.get(String.valueOf(i)).setFlash(0);
                    }
                    postInvalidate();
                    sendUdpDataAdd = 0;
                }
            }
        }
    }

    boolean toPostInvalidate;

    private void setBoldAllChoseColor(int position) {
        toPostInvalidate = false;
        if (!data.get(String.valueOf(position)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position)).isFlash() == 1) != isTwinkle) {
            data.get(String.valueOf(position)).setColor(choseColor);
            data.get(String.valueOf(position)).setShowColor(showColor);
//            data.get(String.valueOf(position)).setLight(choseLight);
            if (isTwinkle) {
                data.get(String.valueOf(position)).setFlash(1);
            } else {
                data.get(String.valueOf(position)).setFlash(0);
            }
            toPostInvalidate = true;
        }
        if (position == 0) {
            LogUtil.e("左上角落点");
            if (!data.get(String.valueOf(position + 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + 1)).setColor(choseColor);
                data.get(String.valueOf(position + 1)).setShowColor(showColor);
//                data.get(String.valueOf(position + 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position + size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + size / column)).setColor(choseColor);
                data.get(String.valueOf(position + size / column)).setShowColor(showColor);
//                data.get(String.valueOf(position + size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else if (position > 0 && position < size / column - 1) {
            LogUtil.e("左边缘点");
            if (!data.get(String.valueOf(position + 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + 1)).setColor(choseColor);
                data.get(String.valueOf(position + 1)).setShowColor(showColor);
//                data.get(String.valueOf(position + 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - 1)).setColor(choseColor);
                data.get(String.valueOf(position - 1)).setShowColor(showColor);
//                data.get(String.valueOf(position - 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position + size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + size / column)).setColor(choseColor);
                data.get(String.valueOf(position + size / column)).setShowColor(showColor);
//                data.get(String.valueOf(position + size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else if (position == size / column - 1) {
            LogUtil.e("左下角落点");
            if (!data.get(String.valueOf(position - 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - 1)).setColor(choseColor);
                data.get(String.valueOf(position - 1)).setShowColor(showColor);
//                data.get(String.valueOf(position - 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position + size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + size / column)).setColor(choseColor);
                data.get(String.valueOf(position + size / column)).setShowColor(showColor);
//                data.get(String.valueOf(position + size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else if (position == data.size() - 1) {
            LogUtil.e("右下角落点");
            if (!data.get(String.valueOf(position - 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - 1)).setColor(choseColor);
                data.get(String.valueOf(position - 1)).setShowColor(showColor);
//                data.get(String.valueOf(position - 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - size / column)).setColor(choseColor);
                data.get(String.valueOf(position - size / column)).setShowColor(showColor);
//                data.get(String.valueOf(position - size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else if (position == data.size() - size / column) {
            LogUtil.e("右上角落点");
            if (!data.get(String.valueOf(position + 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + 1)).setColor(choseColor);
                data.get(String.valueOf(position + 1)).setShowColor(showColor);
//                data.get(String.valueOf(position + 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - size / column)).setColor(choseColor);
                data.get(String.valueOf(position - size / column)).setShowColor(showColor);
//                data.get(String.valueOf(position - size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else if (position % (size / column) == 0) {
            LogUtil.e("上边缘点");
            if (!data.get(String.valueOf(position + 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + 1)).setColor(choseColor);
                data.get(String.valueOf(position + 1)).setShowColor(showColor);
//                data.get(String.valueOf(position + 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - size / column)).setColor(choseColor);
                data.get(String.valueOf(position - size / column)).setShowColor(showColor);
//                data.get(String.valueOf(position - size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position + size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + size / column)).setColor(choseColor);
                data.get(String.valueOf(position + size / column)).setShowColor(showColor);
//                data.get(String.valueOf(position + size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else if ((position + 1) % (size / column) == 0) {
            LogUtil.e("下边缘点");
            if (!data.get(String.valueOf(position - 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - 1)).setColor(choseColor);
                data.get(String.valueOf(position - 1)).setShowColor(showColor);
//                data.get(String.valueOf(position - 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - size / column)).setColor(choseColor);
                data.get(String.valueOf(position - size / column)).setShowColor(showColor);
//                data.get(String.valueOf(position - size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position + size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + size / column)).setColor(choseColor);
                data.get(String.valueOf(position + size / column)).setShowColor(showColor);
//                data.get(String.valueOf(position + size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else if (position < data.size() - 1 && position > data.size() - size / column) {
            LogUtil.e("右边缘点");
            if (!data.get(String.valueOf(position - 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - 1)).setColor(choseColor);
                data.get(String.valueOf(position - 1)).setShowColor(showColor);
//                data.get(String.valueOf(position - 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position + 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + 1)).setColor(choseColor);
                data.get(String.valueOf(position + 1)).setShowColor(showColor);
//                data.get(String.valueOf(position + 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - size / column)).setColor(choseColor);
                data.get(String.valueOf(position - size / column)).setShowColor(showColor);
//                data.get(String.valueOf(position - size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else {
            LogUtil.e("中间点");
            if (!data.get(String.valueOf(position + 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + 1)).setColor(choseColor);
                data.get(String.valueOf(position + 1)).setShowColor(showColor);
//                data.get(String.valueOf(position + 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - 1)).setColor(choseColor);
                data.get(String.valueOf(position - 1)).setShowColor(showColor);
//                data.get(String.valueOf(position - 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - size / column)).setColor(choseColor);
                data.get(String.valueOf(position - size / column)).setShowColor(showColor);
//                data.get(String.valueOf(position - size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position + size / column)).getColor().equalsIgnoreCase(choseColor)) {
                data.get(String.valueOf(position + size / column)).setColor(choseColor);
                data.get(String.valueOf(position + size / column)).setShowColor(showColor);
//                data.get(String.valueOf(position + size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        }
        if (toPostInvalidate) {
            postInvalidate();
            sendUdpDataAdd = 0;
        }
    }

    public void clean() {
        for (int i = 0; i < data.size(); i++) {
            data.get(String.valueOf(i)).setColor("#000000");
            data.get(String.valueOf(i)).setShowColor("#000000");
            data.get(String.valueOf(i)).setFlash(0);
        }
        postInvalidate();
    }


    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    HashMap<String, Doodle> mirror_doodles;

    public void setMirror(int isMirror) {
        this.isMirror = isMirror;
    }

    private HashMap<String, Doodle> toMirror(HashMap<String, Doodle> doodles) {
        mirror_doodles = depCopy(doodles);
        for (int i = 0; i < mirror_doodles.size() / 2; i++) {
            for (int j = 0; j < column / 2; j++) {
                if (i >= mirror_doodles.size() / column * j && i < mirror_doodles.size() / column + mirror_doodles.size() / column * j) {
                    toChangeValue(mirror_doodles, i, (mirror_doodles.size() - (mirror_doodles.size() / column)) - j * 2 * (mirror_doodles.size() / column) + i);
                }
            }
        }
        return mirror_doodles;
    }

    private void toChangeValue(HashMap<String, Doodle> mirror_doodles, int i, int i1) {
        Doodle emp = mirror_doodles.get(String.valueOf(i));
        mirror_doodles.put(String.valueOf(i), mirror_doodles.get(String.valueOf(i1)));
        mirror_doodles.put(String.valueOf(i1), emp);
    }

    /**
     * 深拷贝
     *
     * @param doodles
     * @return
     */
    public HashMap<String, Doodle> depCopy(HashMap<String, Doodle> doodles) {
        HashMap<String, Doodle> destList = new HashMap<String, Doodle>();
        for (Iterator keyIt = doodles.keySet().iterator(); keyIt.hasNext(); ) {
            String key = (String) keyIt.next();
            destList.put(key, doodles.get(key));
        }
        return destList;
    }


    /**
     * 获取Udp实时数据
     *
     * @return
     */
    public byte[] getUdpByteData(HashMap<String, Doodle> data) {
        byte[] upd_data = new byte[1 + 2 + 2 + 3 * size];
        upd_data[0] = 0x01;
        upd_data[1] = 0x00;
        upd_data[2] = 0x00;
        upd_data[3] = ByteUtil.intToByteArray(size)[0];
        upd_data[4] = ByteUtil.intToByteArray(size)[1];
        for (int i = 0; i < size; i++) {
            String color = data.get(String.valueOf(i)).getColor();
            boolean isTwinkle = data.get(String.valueOf(i)).isFlash() == 1;
            if (isTwinkle) {
                if (Math.random() * 10 < 4) {
                    upd_data[i * 3 + 5] = 0x00;
                    upd_data[i * 3 + 6] = 0x00;
                    upd_data[i * 3 + 7] = 0x00;
                } else {
                    upd_data[i * 3 + 5] = (byte) (0xff & Integer.parseInt(color.substring(1, 3), 16));
                    upd_data[i * 3 + 6] = (byte) (0xff & Integer.parseInt(color.substring(3, 5), 16));
                    upd_data[i * 3 + 7] = (byte) (0xff & Integer.parseInt(color.substring(5, 7), 16));
                }
            } else {
                upd_data[i * 3 + 5] = (byte) (0xff & Integer.parseInt(color.substring(1, 3), 16));
                upd_data[i * 3 + 6] = (byte) (0xff & Integer.parseInt(color.substring(3, 5), 16));
                upd_data[i * 3 + 7] = (byte) (0xff & Integer.parseInt(color.substring(5, 7), 16));
            }
        }
        return upd_data;
    }


    public void startSendUpdData() {
        if (isOnline) {
            LogUtil.e("启动发送UDP数据");
            isStopSendUdpData = false;
            sendUdpMessage();
        }
    }

    private int sendUdpDataAdd;
    private ScheduledExecutorService udpExecutorService;
    private ScheduledFuture<?> udpScheduledFuture;
    private Runnable udpTask;

    private void sendUdpMessage() {
        stopSendUdpData();
        sendUdpDataAdd = 0;
        if (udpExecutorService == null) {
            udpExecutorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("udpExecutorService").daemon(true).build());
        }
        if (udpTask == null) {
            udpTask = new Runnable() {
                @Override
                public void run() {
                    if (sendUdpDataAdd != -1) {
                        addMode++;
                        try {
                            if (isStopSendUdpData) {
                                send("255.255.255.255", CommonValue.UDP_PORT, getUdpByteData(cleanData(data)), "画板");
                                stopSendUdpData();
                            } else {
                                send("255.255.255.255", CommonValue.UDP_PORT, getUdpByteData(isMirror == 1 ? toMirror(data) : data), "画板");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            LogUtil.e("发送UDP数据失败");
                        }
                    }
                }
            };
        }
        if (udpExecutorService != null) {
            udpScheduledFuture = udpExecutorService.scheduleAtFixedRate(udpTask, 0, sendDataTime, TimeUnit.MILLISECONDS);
        }
    }


    private void send(String destip, int port, byte[] udpByteData, String type) throws IOException {
        InetAddress address = InetAddress.getByName(destip);
        byte[] send_head_data = ByteUtil.getHeadByteData(udpByteData);
//        LogUtil.e("udpByteData:" + byte2hex(udpByteData));
//        LogUtil.e("send_head_data:" + byte2hex(send_head_data));
        byte[] send_data = ByteUtil.byteMerger(send_head_data, udpByteData);
//        LogUtil.e("send_data:" + byte2hex(send_data));
        // 2.创建数据报，包含发送的数据信息
        DatagramPacket packet = new DatagramPacket(send_data, send_data.length, address, port);
        // 3.创建DatagramSocket对象
        DatagramSocket socket = new DatagramSocket();
        // 4.向服务器端发送数据报
        socket.send(packet);
        // 5.关闭资源
        socket.close();
        LogUtil.e(type + "发送UDP数据成功");
    }

    public String byte2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            //将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            sb.append(tmp + " ");
        }
        return sb.toString();
    }

    public void toStopSendUdpData(boolean isStopSendUdpData) {
        this.isStopSendUdpData = isStopSendUdpData;
    }

    public void stopSendUdpData() {
        if (udpExecutorService != null) {
            LogUtil.e("停止发送数据");
            udpExecutorService.shutdownNow();
        }
        // 非单例模式，置空防止重复的任务
        udpExecutorService = null;
    }

    public void toStopSendUdpModeData(boolean isStopSendUdpModeData, boolean toClean) {
        this.isStopSendUdpModeData = isStopSendUdpModeData;
        this.toClean = toClean;
    }

    public void stopSendUdpModeData() {
        if (modelExecutorService != null) {
            LogUtil.e("停止发送数据");
            modelExecutorService.shutdownNow();
        }
        // 非单例模式，置空防止重复的任务
        modelExecutorService = null;
    }

    public void setAllColor(String chose_color) {
        for (int i = 0; i < data.size(); i++) {
            data.get(String.valueOf(i)).setColor(chose_color);
            data.get(String.valueOf(i)).setShowColor(chose_color);
            data.get(String.valueOf(i)).setFlash(0);
        }
        postInvalidate();
    }

    public void setConfigData(String config_str) {
        this.data_str = config_str;
        init();
    }
}

