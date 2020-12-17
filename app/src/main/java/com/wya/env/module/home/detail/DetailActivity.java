package com.wya.env.module.home.detail;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wya.env.R;
import com.wya.env.base.BaseMvpActivity;
import com.wya.env.bean.doodle.Doodle;
import com.wya.env.bean.doodle.DoodlePattern;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.home.MusicModel;
import com.wya.env.bean.home.MusicSuccess;
import com.wya.env.bean.login.Lamps;
import com.wya.env.bean.tree.TreeData;
import com.wya.env.common.CommonValue;
import com.wya.env.module.home.detail.fragment.CurtainFragment;
import com.wya.env.module.home.detail.fragment.TreeFragment;
import com.wya.env.util.SaveSharedPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static java.lang.Math.tan;

/**
 * @date: 2020/11/4 16:24
 * @author: Chunjiang Mao
 * @classname: DetailActivity
 * @describe: 详情
 */
public class DetailActivity extends BaseMvpActivity<DetailPresent> implements DetailView {
    @BindView(R.id.view)
    FrameLayout view;
    @BindView(R.id.img_music)
    ImageView imgMusic;
    @BindView(R.id.img_like)
    ImageView imgLike;
    @BindView(R.id.img_thin_painter)
    ImageView imgThinPainter;
    @BindView(R.id.ll_edit)
    LinearLayout llEdit;
    @BindView(R.id.ll_save)
    LinearLayout llSave;
    @BindView(R.id.mode_name)
    TextView modeName;

    /**
     * 1 窗帘， 0 圣诞树
     */
    private int type;
    private int position;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private CurtainFragment curtainFragment;
    private TreeFragment treeFragment;
    private String title;
    private int music;


    private String data_str = "{\"Devname\":\"CN3CNT304\",\"Dimension\":\"3\",\"LampTotalNumber\":304,\"AspectRatio\":1.0656,\"AddrTab\":[{\"addr\":15,\"X\": 0.0000,\"Y\": 0.9692,\"Z\":0.5000},{\"addr\":31,\"X\": 0.0271,\"Y\": 0.9692,\"Z\":0.3376},{\"addr\":47,\"X\": 0.1054,\"Y\": 0.9692,\"Z\":0.1929},{\"addr\":63,\"X\": 0.2265,\"Y\": 0.9692,\"Z\":0.0814},{\"addr\":79,\"X\": 0.3773,\"Y\": 0.9692,\"Z\":0.0153},{\"addr\":95,\"X\": 0.5413,\"Y\": 0.9692,\"Z\":0.0017},{\"addr\":111,\"X\": 0.7008,\"Y\": 0.9692,\"Z\":0.0421},{\"addr\":127,\"X\": 0.8386,\"Y\": 0.9692,\"Z\":0.1321},{\"addr\":143,\"X\": 0.9397,\"Y\": 0.9692,\"Z\":0.2620},{\"addr\":159,\"X\": 0.9932,\"Y\": 0.9692,\"Z\":0.4177},{\"addr\":175,\"X\": 0.9932,\"Y\": 0.9692,\"Z\":0.5823},{\"addr\":191,\"X\": 0.9397,\"Y\": 0.9692,\"Z\":0.7380},{\"addr\":207,\"X\": 0.8386,\"Y\": 0.9692,\"Z\":0.8679},{\"addr\":223,\"X\": 0.7008,\"Y\": 0.9692,\"Z\":0.9579},{\"addr\":239,\"X\": 0.5413,\"Y\": 0.9692,\"Z\":0.9983},{\"addr\":255,\"X\": 0.3773,\"Y\": 0.9692,\"Z\":0.9847},{\"addr\":271,\"X\": 0.2265,\"Y\": 0.9692,\"Z\":0.9186},{\"addr\":287,\"X\": 0.1054,\"Y\": 0.9692,\"Z\":0.8071},{\"addr\":303,\"X\": 0.0271,\"Y\": 0.9692,\"Z\":0.6624},{\"addr\":14,\"X\": 0.0305,\"Y\": 0.9066,\"Z\":0.5000},{\"addr\":30,\"X\": 0.0559,\"Y\": 0.9066,\"Z\":0.3475},{\"addr\":46,\"X\": 0.1295,\"Y\": 0.9066,\"Z\":0.2116},{\"addr\":62,\"X\": 0.2432,\"Y\": 0.9066,\"Z\":0.1069},{\"addr\":78,\"X\": 0.3847,\"Y\": 0.9066,\"Z\":0.0448},{\"addr\":94,\"X\": 0.5388,\"Y\": 0.9066,\"Z\":0.0321},{\"addr\":110,\"X\": 0.6886,\"Y\": 0.9066,\"Z\":0.0700},{\"addr\":126,\"X\": 0.8180,\"Y\": 0.9066,\"Z\":0.1546},{\"addr\":142,\"X\": 0.9129,\"Y\": 0.9066,\"Z\":0.2765},{\"addr\":158,\"X\": 0.9631,\"Y\": 0.9066,\"Z\":0.4227},{\"addr\":174,\"X\": 0.9631,\"Y\": 0.9066,\"Z\":0.5773},{\"addr\":190,\"X\": 0.9129,\"Y\": 0.9066,\"Z\":0.7235},{\"addr\":206,\"X\": 0.8180,\"Y\": 0.9066,\"Z\":0.8454},{\"addr\":222,\"X\": 0.6886,\"Y\": 0.9066,\"Z\":0.9300},{\"addr\":238,\"X\": 0.5388,\"Y\": 0.9066,\"Z\":0.9679},{\"addr\":254,\"X\": 0.3847,\"Y\": 0.9066,\"Z\":0.9552},{\"addr\":270,\"X\": 0.2432,\"Y\": 0.9066,\"Z\":0.8931},{\"addr\":286,\"X\": 0.1295,\"Y\": 0.9066,\"Z\":0.7884},{\"addr\":302,\"X\": 0.0559,\"Y\": 0.9066,\"Z\":0.6525},{\"addr\":13,\"X\": 0.0609,\"Y\": 0.8441,\"Z\":0.5000},{\"addr\":29,\"X\": 0.0847,\"Y\": 0.8441,\"Z\":0.3574},{\"addr\":45,\"X\": 0.1535,\"Y\": 0.8441,\"Z\":0.2303},{\"addr\":61,\"X\": 0.2599,\"Y\": 0.8441,\"Z\":0.1324},{\"addr\":77,\"X\": 0.3922,\"Y\": 0.8441,\"Z\":0.0744},{\"addr\":93,\"X\": 0.5363,\"Y\": 0.8441,\"Z\":0.0624},{\"addr\":109,\"X\": 0.6764,\"Y\": 0.8441,\"Z\":0.0979},{\"addr\":125,\"X\": 0.7974,\"Y\": 0.8441,\"Z\":0.1770},{\"addr\":141,\"X\": 0.8861,\"Y\": 0.8441,\"Z\":0.2910},{\"addr\":157,\"X\": 0.9331,\"Y\": 0.8441,\"Z\":0.4277},{\"addr\":173,\"X\": 0.9331,\"Y\": 0.8441,\"Z\":0.5723},{\"addr\":189,\"X\": 0.8861,\"Y\": 0.8441,\"Z\":0.7090},{\"addr\":205,\"X\": 0.7974,\"Y\": 0.8441,\"Z\":0.8230},{\"addr\":221,\"X\": 0.6764,\"Y\": 0.8441,\"Z\":0.9021},{\"addr\":237,\"X\": 0.5363,\"Y\": 0.8441,\"Z\":0.9376},{\"addr\":253,\"X\": 0.3922,\"Y\": 0.8441,\"Z\":0.9256},{\"addr\":269,\"X\": 0.2599,\"Y\": 0.8441,\"Z\":0.8676},{\"addr\":285,\"X\": 0.1535,\"Y\": 0.8441,\"Z\":0.7697},{\"addr\":301,\"X\": 0.0847,\"Y\": 0.8441,\"Z\":0.6426},{\"addr\":12,\"X\": 0.0914,\"Y\": 0.7815,\"Z\":0.5000},{\"addr\":28,\"X\": 0.1135,\"Y\": 0.7815,\"Z\":0.3673},{\"addr\":44,\"X\": 0.1778,\"Y\": 0.7815,\"Z\":0.2490},{\"addr\":60,\"X\": 0.2765,\"Y\": 0.7815,\"Z\":0.1579},{\"addr\":76,\"X\": 0.3997,\"Y\": 0.7815,\"Z\":0.1039},{\"addr\":92,\"X\": 0.5337,\"Y\": 0.7815,\"Z\":0.0928},{\"addr\":108,\"X\": 0.6641,\"Y\": 0.7815,\"Z\":0.1258},{\"addr\":124,\"X\": 0.7767,\"Y\": 0.7815,\"Z\":0.1994},{\"addr\":140,\"X\": 0.8593,\"Y\": 0.7815,\"Z\":0.3055},{\"addr\":156,\"X\": 0.9030,\"Y\": 0.7815,\"Z\":0.4327},{\"addr\":172,\"X\": 0.9030,\"Y\": 0.7815,\"Z\":0.5673},{\"addr\":188,\"X\": 0.8593,\"Y\": 0.7815,\"Z\":0.6945},{\"addr\":204,\"X\": 0.7767,\"Y\": 0.7815,\"Z\":0.8006},{\"addr\":220,\"X\": 0.6641,\"Y\": 0.7815,\"Z\":0.8742},{\"addr\":236,\"X\": 0.5337,\"Y\": 0.7815,\"Z\":0.9072},{\"addr\":252,\"X\": 0.3997,\"Y\": 0.7815,\"Z\":0.8961},{\"addr\":268,\"X\": 0.2765,\"Y\": 0.7815,\"Z\":0.8421},{\"addr\":284,\"X\": 0.1778,\"Y\": 0.7815,\"Z\":0.7510},{\"addr\":300,\"X\": 0.1135,\"Y\": 0.7815,\"Z\":0.6327},{\"addr\":11,\"X\": 0.1219,\"Y\": 0.7190,\"Z\":0.5000},{\"addr\":27,\"X\": 0.1424,\"Y\": 0.7190,\"Z\":0.3772},{\"addr\":43,\"X\": 0.2016,\"Y\": 0.7190,\"Z\":0.2677},{\"addr\":59,\"X\": 0.2932,\"Y\": 0.7190,\"Z\":0.1834},{\"addr\":75,\"X\": 0.4072,\"Y\": 0.7190,\"Z\":0.1335},{\"addr\":91,\"X\": 0.5312,\"Y\": 0.7190,\"Z\":0.1232},{\"addr\":107,\"X\": 0.6519,\"Y\": 0.7190,\"Z\":0.1537},{\"addr\":123,\"X\": 0.7561,\"Y\": 0.7190,\"Z\":0.2218},{\"addr\":139,\"X\": 0.8326,\"Y\": 0.7190,\"Z\":0.3200},{\"addr\":155,\"X\": 0.8730,\"Y\": 0.7190,\"Z\":0.4378},{\"addr\":171,\"X\": 0.8730,\"Y\": 0.7190,\"Z\":0.5622},{\"addr\":187,\"X\": 0.8326,\"Y\": 0.7190,\"Z\":0.6800},{\"addr\":203,\"X\": 0.7561,\"Y\": 0.7190,\"Z\":0.7782},{\"addr\":219,\"X\": 0.6519,\"Y\": 0.7190,\"Z\":0.8463},{\"addr\":235,\"X\": 0.5312,\"Y\": 0.7190,\"Z\":0.8768},{\"addr\":251,\"X\": 0.4072,\"Y\": 0.7190,\"Z\":0.8666},{\"addr\":267,\"X\": 0.2932,\"Y\": 0.7190,\"Z\":0.8166},{\"addr\":283,\"X\": 0.2016,\"Y\": 0.7190,\"Z\":0.7323},{\"addr\":299,\"X\": 0.1424,\"Y\": 0.7190,\"Z\":0.6228},{\"addr\":10,\"X\": 0.1523,\"Y\": 0.6564,\"Z\":0.5000},{\"addr\":26,\"X\": 0.1712,\"Y\": 0.6564,\"Z\":0.3871},{\"addr\":42,\"X\": 0.2257,\"Y\": 0.6564,\"Z\":0.2865},{\"addr\":58,\"X\": 0.3098,\"Y\": 0.6564,\"Z\":0.2090},{\"addr\":74,\"X\": 0.4147,\"Y\": 0.6564,\"Z\":0.1630},{\"addr\":90,\"X\": 0.5287,\"Y\": 0.6564,\"Z\":0.1535},{\"addr\":106,\"X\": 0.6397,\"Y\": 0.6564,\"Z\":0.1816},{\"addr\":122,\"X\": 0.7355,\"Y\": 0.6564,\"Z\":0.2442},{\"addr\":138,\"X\": 0.8058,\"Y\": 0.6564,\"Z\":0.3345},{\"addr\":154,\"X\": 0.8429,\"Y\": 0.6564,\"Z\":0.4428},{\"addr\":170,\"X\": 0.8429,\"Y\": 0.6564,\"Z\":0.5572},{\"addr\":186,\"X\": 0.8058,\"Y\": 0.6564,\"Z\":0.6655},{\"addr\":202,\"X\": 0.7355,\"Y\": 0.6564,\"Z\":0.7558},{\"addr\":218,\"X\": 0.6397,\"Y\": 0.6564,\"Z\":0.8184},{\"addr\":234,\"X\": 0.5287,\"Y\": 0.6564,\"Z\":0.8465},{\"addr\":250,\"X\": 0.4147,\"Y\": 0.6564,\"Z\":0.8370},{\"addr\":266,\"X\": 0.3098,\"Y\": 0.6564,\"Z\":0.7910},{\"addr\":282,\"X\": 0.2257,\"Y\": 0.6564,\"Z\":0.7135},{\"addr\":298,\"X\": 0.1712,\"Y\": 0.6564,\"Z\":0.6129},{\"addr\":9,\"X\": 0.1828,\"Y\": 0.5938,\"Z\":0.5000},{\"addr\":25,\"X\": 0.2000,\"Y\": 0.5938,\"Z\":0.3970},{\"addr\":41,\"X\": 0.2497,\"Y\": 0.5938,\"Z\":0.3052},{\"addr\":57,\"X\": 0.3265,\"Y\": 0.5938,\"Z\":0.2345},{\"addr\":73,\"X\": 0.4221,\"Y\": 0.5938,\"Z\":0.1925},{\"addr\":89,\"X\": 0.5262,\"Y\": 0.5938,\"Z\":0.1839},{\"addr\":105,\"X\": 0.6274,\"Y\": 0.5938,\"Z\":0.2095},{\"addr\":121,\"X\": 0.7148,\"Y\": 0.5938,\"Z\":0.2666},{\"addr\":137,\"X\": 0.7790,\"Y\": 0.5938,\"Z\":0.3490},{\"addr\":153,\"X\": 0.8129,\"Y\": 0.5938,\"Z\":0.4478},{\"addr\":169,\"X\": 0.8129,\"Y\": 0.5938,\"Z\":0.5522},{\"addr\":185,\"X\": 0.7790,\"Y\": 0.5938,\"Z\":0.6510},{\"addr\":201,\"X\": 0.7148,\"Y\": 0.5938,\"Z\":0.7334},{\"addr\":217,\"X\": 0.6274,\"Y\": 0.5938,\"Z\":0.7905},{\"addr\":233,\"X\": 0.5262,\"Y\": 0.5938,\"Z\":0.8161},{\"addr\":249,\"X\": 0.4221,\"Y\": 0.5938,\"Z\":0.8075},{\"addr\":265,\"X\": 0.3265,\"Y\": 0.5938,\"Z\":0.6948},{\"addr\":281,\"X\": 0.2497,\"Y\": 0.5938,\"Z\":0.6030},{\"addr\":297,\"X\": 0.2000,\"Y\": 0.5938,\"Z\":0.6030},{\"addr\":8,\"X\": 0.2133,\"Y\": 0.5313,\"Z\":0.5000},{\"addr\":24,\"X\": 0.2288,\"Y\": 0.5313,\"Z\":0.4069},{\"addr\":40,\"X\": 0.2737,\"Y\": 0.5313,\"Z\":0.3239},{\"addr\":56,\"X\": 0.3432,\"Y\": 0.5313,\"Z\":0.2600},{\"addr\":72,\"X\": 0.4296,\"Y\": 0.5313,\"Z\":0.2221},{\"addr\":88,\"X\": 0.5237,\"Y\": 0.5313,\"Z\":0.2143},{\"addr\":104,\"X\": 0.6152,\"Y\": 0.5313,\"Z\":0.2374},{\"addr\":120,\"X\": 0.6942,\"Y\": 0.5313,\"Z\":0.2891},{\"addr\":136,\"X\": 0.7522,\"Y\": 0.5313,\"Z\":0.3635},{\"addr\":152,\"X\": 0.7828,\"Y\": 0.5313,\"Z\":0.4528},{\"addr\":168,\"X\": 0.7828,\"Y\": 0.5313,\"Z\":0.5472},{\"addr\":184,\"X\": 0.7522,\"Y\": 0.5313,\"Z\":0.6365},{\"addr\":200,\"X\": 0.6942,\"Y\": 0.5313,\"Z\":0.7109},{\"addr\":216,\"X\": 0.6152,\"Y\": 0.5313,\"Z\":0.7626},{\"addr\":232,\"X\": 0.5237,\"Y\": 0.5313,\"Z\":0.7857},{\"addr\":248,\"X\": 0.4296,\"Y\": 0.5313,\"Z\":0.7779},{\"addr\":264,\"X\": 0.3432,\"Y\": 0.5313,\"Z\":0.7400},{\"addr\":280,\"X\": 0.2737,\"Y\": 0.5313,\"Z\":0.6761},{\"addr\":296,\"X\": 0.2288,\"Y\": 0.5313,\"Z\":0.5931},{\"addr\":7,\"X\": 0.2438,\"Y\": 0.4687,\"Z\":0.5000},{\"addr\":23,\"X\": 0.2576,\"Y\": 0.4687,\"Z\":0.4168},{\"addr\":39,\"X\": 0.2978,\"Y\": 0.4687,\"Z\":0.3426},{\"addr\":55,\"X\": 0.3598,\"Y\": 0.4687,\"Z\":0.2855},{\"addr\":71,\"X\": 0.4371,\"Y\": 0.4687,\"Z\":0.2516},{\"addr\":87,\"X\": 0.5212,\"Y\": 0.4687,\"Z\":0.2446},{\"addr\":103,\"X\": 0.6029,\"Y\": 0.4687,\"Z\":0.2653},{\"addr\":119,\"X\": 0.6735,\"Y\": 0.4687,\"Z\":0.3115},{\"addr\":135,\"X\": 0.7254,\"Y\": 0.4687,\"Z\":0.3780},{\"addr\":151,\"X\": 0.7528,\"Y\": 0.4687,\"Z\":0.4578},{\"addr\":167,\"X\": 0.7528,\"Y\": 0.4687,\"Z\":0.5422},{\"addr\":183,\"X\": 0.7254,\"Y\": 0.4687,\"Z\":0.6220},{\"addr\":199,\"X\": 0.6735,\"Y\": 0.4687,\"Z\":0.6885},{\"addr\":215,\"X\": 0.6029,\"Y\": 0.4687,\"Z\":0.7347},{\"addr\":231,\"X\": 0.5212,\"Y\": 0.4687,\"Z\":0.7554},{\"addr\":247,\"X\": 0.4371,\"Y\": 0.4687,\"Z\":0.7484},{\"addr\":263,\"X\": 0.3598,\"Y\": 0.4687,\"Z\":0.7145},{\"addr\":279,\"X\": 0.2978,\"Y\": 0.4687,\"Z\":0.6574},{\"addr\":295,\"X\": 0.2576,\"Y\": 0.4687,\"Z\":0.5832},{\"addr\":6,\"X\": 0.2742,\"Y\": 0.4062,\"Z\":0.5000},{\"addr\":22,\"X\": 0.2865,\"Y\": 0.4062,\"Z\":0.4267},{\"addr\":38,\"X\": 0.3218,\"Y\": 0.4062,\"Z\":0.3613},{\"addr\":54,\"X\": 0.3765,\"Y\": 0.4062,\"Z\":0.3110},{\"addr\":70,\"X\": 0.4446,\"Y\": 0.4062,\"Z\":0.2811},{\"addr\":86,\"X\": 0.5186,\"Y\": 0.4062,\"Z\":0.2750},{\"addr\":102,\"X\": 0.5907,\"Y\": 0.4062,\"Z\":0.2932},{\"addr\":118,\"X\": 0.6529,\"Y\": 0.4062,\"Z\":0.3339},{\"addr\":134,\"X\": 0.6986,\"Y\": 0.4062,\"Z\":0.3925},{\"addr\":150,\"X\": 0.7227,\"Y\": 0.4062,\"Z\":0.4628},{\"addr\":166,\"X\": 0.7227,\"Y\": 0.4062,\"Z\":0.5372},{\"addr\":182,\"X\": 0.6986,\"Y\": 0.4062,\"Z\":0.6075},{\"addr\":198,\"X\": 0.6529,\"Y\": 0.4062,\"Z\":0.6661},{\"addr\":214,\"X\": 0.5907,\"Y\": 0.4062,\"Z\":0.7068},{\"addr\":230,\"X\": 0.5186,\"Y\": 0.4062,\"Z\":0.7250},{\"addr\":246,\"X\": 0.4446,\"Y\": 0.4062,\"Z\":0.7189},{\"addr\":262,\"X\": 0.3765,\"Y\": 0.4062,\"Z\":0.6890},{\"addr\":278,\"X\": 0.3218,\"Y\": 0.4062,\"Z\":0.6387},{\"addr\":294,\"X\": 0.2865,\"Y\": 0.4062,\"Z\":0.5733},{\"addr\":5,\"X\": 0.3047,\"Y\": 0.3436,\"Z\":0.5000},{\"addr\":21,\"X\": 0.3153,\"Y\": 0.3436,\"Z\":0.4366},{\"addr\":37,\"X\": 0.3459,\"Y\": 0.3436,\"Z\":0.3800},{\"addr\":53,\"X\": 0.3932,\"Y\": 0.3436,\"Z\":0.3365},{\"addr\":69,\"X\": 0.4521,\"Y\": 0.3436,\"Z\":0.3107},{\"addr\":85,\"X\": 0.5161,\"Y\": 0.3436,\"Z\":0.3054},{\"addr\":101,\"X\": 0.5785,\"Y\": 0.3436,\"Z\":0.3211},{\"addr\":117,\"X\": 0.6323,\"Y\": 0.3436,\"Z\":0.3563},{\"addr\":133,\"X\": 0.6718,\"Y\": 0.3436,\"Z\":0.4070},{\"addr\":149,\"X\": 0.6926,\"Y\": 0.3436,\"Z\":0.4679},{\"addr\":165,\"X\": 0.6926,\"Y\": 0.3436,\"Z\":0.5321},{\"addr\":181,\"X\": 0.6718,\"Y\": 0.3436,\"Z\":0.5930},{\"addr\":197,\"X\": 0.6323,\"Y\": 0.3436,\"Z\":0.6437},{\"addr\":213,\"X\": 0.5785,\"Y\": 0.3436,\"Z\":0.6789},{\"addr\":229,\"X\": 0.5161,\"Y\": 0.3436,\"Z\":0.6946},{\"addr\":245,\"X\": 0.4521,\"Y\": 0.3436,\"Z\":0.6893},{\"addr\":261,\"X\": 0.3932,\"Y\": 0.3436,\"Z\":0.6635},{\"addr\":277,\"X\": 0.3459,\"Y\": 0.3436,\"Z\":0.6200},{\"addr\":293,\"X\": 0.3153,\"Y\": 0.3436,\"Z\":0.5634},{\"addr\":4,\"X\": 0.3352,\"Y\": 0.2810,\"Z\":0.5000},{\"addr\":20,\"X\": 0.3441,\"Y\": 0.2810,\"Z\":0.4465},{\"addr\":36,\"X\": 0.3699,\"Y\": 0.2810,\"Z\":0.3988},{\"addr\":52,\"X\": 0.4098,\"Y\": 0.2810,\"Z\":0.3620},{\"addr\":68,\"X\": 0.4595,\"Y\": 0.2810,\"Z\":0.3402},{\"addr\":84,\"X\": 0.5136,\"Y\": 0.2810,\"Z\":0.3357},{\"addr\":100,\"X\": 0.5662,\"Y\": 0.2810,\"Z\":0.3490},{\"addr\":116,\"X\": 0.6116,\"Y\": 0.2810,\"Z\":0.3787},{\"addr\":132,\"X\": 0.6450,\"Y\": 0.2810,\"Z\":0.4215},{\"addr\":148,\"X\": 0.6626,\"Y\": 0.2810,\"Z\":0.4729},{\"addr\":164,\"X\": 0.6626,\"Y\": 0.2810,\"Z\":0.5271},{\"addr\":180,\"X\": 0.6450,\"Y\": 0.2810,\"Z\":0.5785},{\"addr\":196,\"X\": 0.6116,\"Y\": 0.2810,\"Z\":0.6213},{\"addr\":212,\"X\": 0.5662,\"Y\": 0.2810,\"Z\":0.6510},{\"addr\":228,\"X\": 0.5136,\"Y\": 0.2810,\"Z\":0.6643},{\"addr\":244,\"X\": 0.4595,\"Y\": 0.2810,\"Z\":0.6598},{\"addr\":260,\"X\": 0.4098,\"Y\": 0.2810,\"Z\":0.6380},{\"addr\":276,\"X\": 0.3699,\"Y\": 0.2810,\"Z\":0.6012},{\"addr\":292,\"X\": 0.3441,\"Y\": 0.2810,\"Z\":0.5535},{\"addr\":3,\"X\": 0.3656,\"Y\": 0.2185,\"Z\":0.5000},{\"addr\":19,\"X\": 0.3729,\"Y\": 0.2185,\"Z\":0.4564},{\"addr\":35,\"X\": 0.3940,\"Y\": 0.2185,\"Z\":0.4175},{\"addr\":51,\"X\": 0.4265,\"Y\": 0.2185,\"Z\":0.3875},{\"addr\":67,\"X\": 0.4670,\"Y\": 0.2185,\"Z\":0.3697},{\"addr\":83,\"X\": 0.5111,\"Y\": 0.2185,\"Z\":0.3661},{\"addr\":99,\"X\": 0.5540,\"Y\": 0.2185,\"Z\":0.3769},{\"addr\":115,\"X\": 0.5910,\"Y\": 0.2185,\"Z\":0.4011},{\"addr\":131,\"X\": 0.6182,\"Y\": 0.2185,\"Z\":0.4360},{\"addr\":147,\"X\": 0.6325,\"Y\": 0.2185,\"Z\":0.4779},{\"addr\":163,\"X\": 0.6325,\"Y\": 0.2185,\"Z\":0.5221},{\"addr\":179,\"X\": 0.6182,\"Y\": 0.2185,\"Z\":0.5640},{\"addr\":195,\"X\": 0.5910,\"Y\": 0.2185,\"Z\":0.5989},{\"addr\":211,\"X\": 0.5540,\"Y\": 0.2185,\"Z\":0.6231},{\"addr\":227,\"X\": 0.5111,\"Y\": 0.2185,\"Z\":0.6339},{\"addr\":243,\"X\": 0.4670,\"Y\": 0.2185,\"Z\":0.6303},{\"addr\":259,\"X\": 0.4265,\"Y\": 0.2185,\"Z\":0.6125},{\"addr\":275,\"X\": 0.3940,\"Y\": 0.2185,\"Z\":0.5825},{\"addr\":291,\"X\": 0.3729,\"Y\": 0.2185,\"Z\":0.5436},{\"addr\":2,\"X\": 0.3961,\"Y\": 0.1559,\"Z\":0.5000},{\"addr\":18,\"X\": 0.4017,\"Y\": 0.1559,\"Z\":0.4663},{\"addr\":34,\"X\": 0.4180,\"Y\": 0.1559,\"Z\":0.4362},{\"addr\":50,\"X\": 0.4432,\"Y\": 0.1559,\"Z\":0.4130},{\"addr\":66,\"X\": 0.4745,\"Y\": 0.1559,\"Z\":0.3993},{\"addr\":82,\"X\": 0.5086,\"Y\": 0.1559,\"Z\":0.3965},{\"addr\":98,\"X\": 0.5417,\"Y\": 0.1559,\"Z\":0.4049},{\"addr\":114,\"X\": 0.5704,\"Y\": 0.1559,\"Z\":0.4236},{\"addr\":130,\"X\": 0.5914,\"Y\": 0.1559,\"Z\":0.4505},{\"addr\":146,\"X\": 0.6025,\"Y\": 0.1559,\"Z\":0.4829},{\"addr\":162,\"X\": 0.6025,\"Y\": 0.1559,\"Z\":0.5171},{\"addr\":178,\"X\": 0.5914,\"Y\": 0.1559,\"Z\":0.5495},{\"addr\":194,\"X\": 0.5704,\"Y\": 0.1559,\"Z\":0.5764},{\"addr\":210,\"X\": 0.5417,\"Y\": 0.1559,\"Z\":0.5951},{\"addr\":226,\"X\": 0.5086,\"Y\": 0.1559,\"Z\":0.6035},{\"addr\":242,\"X\": 0.4745,\"Y\": 0.1559,\"Z\":0.6007},{\"addr\":258,\"X\": 0.4432,\"Y\": 0.1559,\"Z\":0.5870},{\"addr\":274,\"X\": 0.4180,\"Y\": 0.1559,\"Z\":0.5638},{\"addr\":290,\"X\": 0.4017,\"Y\": 0.1559,\"Z\":0.5337},{\"addr\":1,\"X\": 0.4266,\"Y\": 0.0934,\"Z\":0.5000},{\"addr\":17,\"X\": 0.4306,\"Y\": 0.0934,\"Z\":0.4762},{\"addr\":33,\"X\": 0.4421,\"Y\": 0.0934,\"Z\":0.4549},{\"addr\":49,\"X\": 0.4598,\"Y\": 0.0934,\"Z\":0.4385},{\"addr\":65,\"X\": 0.4820,\"Y\": 0.0934,\"Z\":0.4288},{\"addr\":81,\"X\": 0.5061,\"Y\": 0.0934,\"Z\":0.4268},{\"addr\":97,\"X\": 0.5295,\"Y\": 0.0934,\"Z\":0.4328},{\"addr\":113,\"X\": 0.5497,\"Y\": 0.0934,\"Z\":0.4460},{\"addr\":129,\"X\": 0.5646,\"Y\": 0.0934,\"Z\":0.4651},{\"addr\":145,\"X\": 0.5724,\"Y\": 0.0934,\"Z\":0.4879},{\"addr\":161,\"X\": 0.5724,\"Y\": 0.0934,\"Z\":0.5121},{\"addr\":177,\"X\": 0.5646,\"Y\": 0.0934,\"Z\":0.5349},{\"addr\":193,\"X\": 0.5497,\"Y\": 0.0934,\"Z\":0.5540},{\"addr\":209,\"X\": 0.5295,\"Y\": 0.0934,\"Z\":0.5672},{\"addr\":225,\"X\": 0.5061,\"Y\": 0.0934,\"Z\":0.5732},{\"addr\":241,\"X\": 0.4820,\"Y\": 0.0934,\"Z\":0.5712},{\"addr\":257,\"X\": 0.4598,\"Y\": 0.0934,\"Z\":0.5615},{\"addr\":273,\"X\": 0.4421,\"Y\": 0.0934,\"Z\":0.5451},{\"addr\":289,\"X\": 0.4306,\"Y\": 0.0934,\"Z\":0.5238},{\"addr\":0,\"X\": 0.4570,\"Y\": 0.0308,\"Z\":0.5000},{\"addr\":16,\"X\": 0.4594,\"Y\": 0.0308,\"Z\":0.4861},{\"addr\":32,\"X\": 0.4661,\"Y\": 0.0308,\"Z\":0.4736},{\"addr\":48,\"X\": 0.4765,\"Y\": 0.0308,\"Z\":0.4640},{\"addr\":64,\"X\": 0.4895,\"Y\": 0.0308,\"Z\":0.4584},{\"addr\":80,\"X\": 0.5035,\"Y\": 0.0308,\"Z\":0.4572},{\"addr\":96,\"X\": 0.5173,\"Y\": 0.0308,\"Z\":0.4607},{\"addr\":112,\"X\": 0.5291,\"Y\": 0.0308,\"Z\":0.4684},{\"addr\":128,\"X\": 0.5378,\"Y\": 0.0308,\"Z\":0.4796},{\"addr\":144,\"X\": 0.5424,\"Y\": 0.0308,\"Z\":0.4929},{\"addr\":160,\"X\": 0.5424,\"Y\": 0.0308,\"Z\":0.5071},{\"addr\":176,\"X\": 0.5378,\"Y\": 0.0308,\"Z\":0.5204},{\"addr\":192,\"X\": 0.5291,\"Y\": 0.0308,\"Z\":0.5316},{\"addr\":208,\"X\": 0.5173,\"Y\": 0.0308,\"Z\":0.5393},{\"addr\":224,\"X\": 0.5035,\"Y\": 0.0308,\"Z\":0.5428},{\"addr\":240,\"X\": 0.4895,\"Y\": 0.0308,\"Z\":0.5417},{\"addr\":256,\"X\": 0.4765,\"Y\": 0.0308,\"Z\":0.5360},{\"addr\":272,\"X\": 0.4661,\"Y\": 0.0308,\"Z\":0.5264},{\"addr\":288,\"X\": 0.4594,\"Y\": 0.0308,\"Z\":0.5139}]}";

    private TreeData treeData;
    private List<Doodle> treeDoodles;

    @Override
    protected void initView() {
        title = getIntent().getStringExtra("title");
        type = getIntent().getIntExtra("type", 1);
        position = getIntent().getIntExtra("position", 1);
        music = getIntent().getIntExtra("music", 1);

        setTitle(title);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (type == 1) {
            curtainFragment = new CurtainFragment(getModels(position, type));
            fragmentTransaction.add(R.id.view, curtainFragment);
            fragmentTransaction.show(curtainFragment).commit();
        } else {
            treeFragment = new TreeFragment(getModels(position, type));
            fragmentTransaction.add(R.id.view, treeFragment);
            fragmentTransaction.show(treeFragment).commit();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicSuccess event) {
        music = 1 - music;
        if (music == 1) {
            imgMusic.setImageDrawable(getResources().getDrawable(R.drawable.yinyueshibie));
        } else {
            imgMusic.setImageDrawable(getResources().getDrawable(R.drawable.yinyuemoren));
        }
    }

    @OnClick({R.id.img_music, R.id.img_like, R.id.ll_edit, R.id.ll_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_music:
                MusicModel musicModel = new MusicModel();
                musicModel.setPosition(position);
                musicModel.setMusic(music);
                musicModel.setClick(true);
                EventBus.getDefault().post(musicModel);
                break;
            case R.id.img_like:
                break;
            case R.id.ll_edit:
                break;
            case R.id.ll_save:
                break;
        }
    }


    int column;
    int size;
    int row;
    private LampModel lampModel;
    private Lamps lamps;

    private LampModel getModels(int position, int type) {
        lamps = new Gson().fromJson(SaveSharedPreferences.getString(this, CommonValue.LAMPS), Lamps.class);
        if (lamps != null && lamps.getColumn() != 0 && lamps.getRow() != 0) {
            column = lamps.getColumn();
            row = lamps.getRow();
            size = lamps.getSize();
        } else {
            column = 15;
            row = 15;
            size = 225;
        }
        switch (position) {
            case 0:
                lampModel = getModel1(type);
                break;
            case 1:
                lampModel = getModel2(type);
                break;
            case 2:
                lampModel = getModel3();
                break;
            case 3:
                lampModel = getModel4();
                break;
            case 4:
                lampModel = getModel5();
                break;
            case 5:
                lampModel = getModel6();
                break;
            case 6:
                lampModel = getModel7();
                break;
            case 7:
                lampModel = getModel8();
                break;
            case 8:
                lampModel = getModel9();
                break;
            case 9:
                lampModel = getModel10();
                break;
            default:
                break;
        }
        initCurtainData(lampModel);
        return lampModel;
    }

    private void initCurtainData(LampModel lampModel) {
        modeName.setText(lampModel.getName());
        if (music == 1) {
            imgMusic.setImageDrawable(getResources().getDrawable(R.drawable.yinyueshibie));
        } else {
            imgMusic.setImageDrawable(getResources().getDrawable(R.drawable.yinyuemoren));
        }

        MusicModel musicModel = new MusicModel();
        musicModel.setMusic(music);
        musicModel.setPosition(position);
        musicModel.setClick(false);
        EventBus.getDefault().post(musicModel);
    }

    private LampModel getModel10() {
        LampModel lampModel = new LampModel();
        lampModel.setName("Bright Delightlux");
        List<DoodlePattern> modeArr = new ArrayList<>();


        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();

                int w = (int) (Math.random() * 10);
                if (w == 6) {
                    doodle.setColor("#FF00FF");
                } else if (w == 3) {
                    doodle.setColor("#FFFFFF");
                } else {
                    doodle.setColor("#000000");
                }

                doodle.setFlash(0);
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    private LampModel getModel9() {
        String[] colorHexArr = {"#FF0000", "#00FF00", "#FFFFFF", "#000000", "#007FFF", "#0000FF", "#8B00FF"};
        LampModel lampModel = new LampModel();
        lampModel.setName("Glow");
        List<DoodlePattern> modeArr = new ArrayList<>();

        for (int k = 0; k < 2; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();
                doodle.setColor(colorHexArr[(i % row - 0 + row + 1) / 1 % 4]);
                doodle.setFlash(0);
                int x = (int) (Math.random() * 2);
                if (x == 1) {
                    doodle.setColor("#000000");
                }
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    private LampModel getModel8() {

        String[] colorHexArr = {"#FA0000", "#FAA500", "#FAFF00", "#00FF00", "#007FFF", "#0000FF", "#8B00FF"};
        LampModel lampModel = new LampModel();
        lampModel.setName("Vertical");
        List<DoodlePattern> modeArr = new ArrayList<>();

        for (int k = 0; k < column; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();
                doodle.setColor(colorHexArr[(i / row - k + column + 1) / 3 % 7]);

                doodle.setFlash(0);
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;

    }

    private LampModel getModel7() {
        String[] colors = {"#FA0000", "#FAA500", "#00FF00"};
        LampModel lampModel = new LampModel();
        lampModel.setName("Sunset");
        List<DoodlePattern> modeArr = new ArrayList<>();

        int alpha = 14;
        int beta = 7;
        int gama = 0;
        for (int i = 0; i < 21; i++) {
            double a = tan((alpha + i) % 21 * Math.PI / 42);
            double b = tan((beta + i) % 21 * Math.PI / 42);
            double c = tan((gama + i) % 21 * Math.PI / 42);

            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int j = 0; j < size; j++) {
                Doodle doodle = new Doodle();
                doodle.setColor("#000000");

                doodle.setFlash(0);
                light_status.put(String.valueOf(j), doodle);

                double l = j;

                if (a > b && b > c) {
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= a) {
                        doodle.setColor(colors[0]);
                        doodle.setFlash(0);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < a && (double) (row - 1 - j % row) / (double) (j / row + 1) >= b) {
                        doodle.setColor(colors[1]);
                        doodle.setFlash(0);

                        light_status.put(String.valueOf(j), doodle);

                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < b && (double) (row - 1 - j % row) / (double) (j / row + 1) >= c) {
                        doodle.setColor(colors[2]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < c) {
                        doodle.setColor(colors[0]);
                        doodle.setFlash(0);

                        light_status.put(String.valueOf(j), doodle);
                    }
                }

                if (a < c && b > c) {
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= b) {
                        doodle.setColor(colors[1]);
                        doodle.setFlash(1);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < b && (double) (row - 1 - j % row) / (double) (j / row + 1) >= c) {
                        doodle.setColor(colors[2]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= a && (double) (row - 1 - j % row) / (double) (j / row + 1) < c) {
                        doodle.setColor(colors[0]);
                        doodle.setFlash(0);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < a) {
                        doodle.setColor(colors[1]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                }

                if (a > b && b < c) {
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= c) {
                        doodle.setColor(colors[2]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < c && (double) (row - 1 - j % row) / (double) (j / row + 1) >= a) {
                        doodle.setColor(colors[0]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= b && (double) (row - 1 - j % row) / (double) (j / row + 1) < a) {
                        doodle.setColor(colors[1]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < b) {
                        doodle.setColor(colors[2]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                }

            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);

        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }


    private LampModel getModel6() {
        String[] colorHexArr = {"#FA0000", "#FAA500", "#000000", "#00FF00", "#007FFF", "#000000", "#8B00FF"};
        LampModel lampModel = new LampModel();
        lampModel.setName("Updown");
        List<DoodlePattern> modeArr = new ArrayList<>();

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();
                doodle.setColor(colorHexArr[(i % row - k + row + 1) / 3 % 7]);

                doodle.setFlash(0);
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    private LampModel getModel5() {
        String[] colorHexArr = {"#FA0000", "#FAA500", "#FAFF00", "#00FF00", "#007FFF", "#0000FF", "#8B00FF"};
        LampModel lampModel = new LampModel();
        lampModel.setName("Horizontal Flag");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();
                doodle.setColor(colorHexArr[(i % row - k + row + 1) / 3 % 7]);

                doodle.setFlash(0);
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }


    private LampModel getModel4() {
        LampModel lampModel = new LampModel();
        lampModel.setName("Sparkles");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#F99601");
                    } else {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    doodle.setColor("#000000");
                    if (k != 0) {
                        int x = (int) (Math.random() * 2);
                        if (x == 1) {
                            doodle.setColor("#000000");
                        }
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    private LampModel getModel1(int type) {
        LampModel lampModel = new LampModel();
        lampModel.setName("Diagonal");
        if (type == 1) {
            List<DoodlePattern> modeArr = new ArrayList<>();
            for (int k = 0; k < size / column; k++) {
                DoodlePattern doodlePattern = new DoodlePattern();
                HashMap<String, Doodle> light_status = new HashMap<>();
                for (int i = 0; i < column; i++) {
                    for (int j = 0; j < size / column; j++) {
                        Doodle doodle = new Doodle();
                        if (j % (size / column) == (i + k) % (size / column) || j % (size / column) == (i + k + 1) % (size / column) || j % (size / column) == (i + k + 2) % (size / column) || j % (size / column) == (i + k + 3) % (size / column)) {
                            doodle.setColor("#ff0000");
                        } else if (j % (size / column) == (i + k + 8) % (size / column) || j % (size / column) == (i + k + 9) % (size / column) || j % (size / column) == (i + k + 10) % (size / column) || j % (size / column) == (i + k + 11) % (size / column)) {
                            doodle.setColor("#ffffff");
                        } else {
                            doodle.setColor("#F2E93F");
                        }
                        doodle.setFlash(0);
                        light_status.put(String.valueOf(i * size / column + j), doodle);
                    }
                }
                doodlePattern.setLight_status(light_status);
                doodlePattern.setSize(size);
                modeArr.add(doodlePattern);
            }
            lampModel.setModeArr(modeArr);
            lampModel.setLight(100);
            lampModel.setSize(size);
            lampModel.setLightRow(size / column);
            lampModel.setColumn(column);
        } else {
            treeData = new Gson().fromJson(data_str, TreeData.class);
            treeDoodles = treeData.getAddrTab();
            size = 304;
            column = 19;
            List<DoodlePattern> modeArr = new ArrayList<>();
            for (int k = 0; k < size / column; k++) {
                DoodlePattern doodlePattern = new DoodlePattern();
                HashMap<String, Doodle> light_status = new HashMap<>();
                for (int i = 0; i < column; i++) {
                    for (int j = 0; j < size / column; j++) {
                        Doodle doodle = new Doodle();
                        if (j % (size / column) == (i + k) % (size / column) || j % (size / column) == (i + k + 1) % (size / column) || j % (size / column) == (i + k + 2) % (size / column) || j % (size / column) == (i + k + 3) % (size / column)) {
                            doodle.setColor("#ff0000");
                        } else if (j % (size / column) == (i + k + 8) % (size / column) || j % (size / column) == (i + k + 9) % (size / column) || j % (size / column) == (i + k + 10) % (size / column) || j % (size / column) == (i + k + 11) % (size / column)) {
                            doodle.setColor("#ffffff");
                        } else {
                            doodle.setColor("#F2E93F");
                        }
                        light_status.put(String.valueOf(i * size / column + j), doodle);
                    }
                }
                doodlePattern.setLight_status(getLightStatus(light_status));
                doodlePattern.setSize(size);
                modeArr.add(doodlePattern);
            }
            lampModel.setModeArr(modeArr);
            lampModel.setLight(100);
            lampModel.setSize(size);
            lampModel.setLightRow(size / column);
            lampModel.setColumn(column);
        }
        return lampModel;
    }

    private LampModel getModel2(int type) {
        LampModel lampModel = new LampModel();
        lampModel.setName("Fireworks");
        if (type == 0) {
            treeData = new Gson().fromJson(data_str, TreeData.class);
            treeDoodles = treeData.getAddrTab();
            size = 304;
            column = 19;
            row = 16;
        }
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();

                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#ff0000");
                    } else {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);


                }
            }
            if(type == 0){
                doodlePattern.setLight_status(getLightStatus(light_status));
            } else {
                doodlePattern.setLight_status(light_status);
            }

            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();

                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#00ff00");
                    } else {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            if(type == 0){
                doodlePattern.setLight_status(getLightStatus(light_status));
            } else {
                doodlePattern.setLight_status(light_status);
            }
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);

        }
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#0000ff");
                    } else {
                        doodle.setColor("#000000");
                    }


                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            if(type == 0){
                doodlePattern.setLight_status(getLightStatus(light_status));
            } else {
                doodlePattern.setLight_status(light_status);
            }
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#ffffff");
                    } else {
                        doodle.setColor("#000000");
                    }
                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            if(type == 0){
                doodlePattern.setLight_status(getLightStatus(light_status));
            } else {
                doodlePattern.setLight_status(light_status);
            }
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }


    private LampModel getModel3() {
        LampModel lampModel = new LampModel();
        lampModel.setName("Waves");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#ff0000");
                    } else {
                        doodle.setColor("#000000");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();

                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#000000");
                    } else {
                        doodle.setColor("#ff0000");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#00ff00");
                    } else {
                        doodle.setColor("#000000");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();

                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#000000");
                    } else {
                        doodle.setColor("#00ff00");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#0000ff");
                    } else {
                        doodle.setColor("#000000");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#000000");
                    } else {
                        doodle.setColor("#0000ff");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }


    private HashMap<String, Doodle> getLightStatus(HashMap<String, Doodle> light_status) {
        for (int i = 0; i < treeDoodles.size(); i++) {
            light_status.get(String.valueOf(treeDoodles.get(i).getAddr())).setAddr(treeDoodles.get(i).getAddr());
            light_status.get(String.valueOf(treeDoodles.get(i).getAddr())).setX(treeDoodles.get(i).getX());
            light_status.get(String.valueOf(treeDoodles.get(i).getAddr())).setY(treeDoodles.get(i).getY());
            light_status.get(String.valueOf(treeDoodles.get(i).getAddr())).setZ(treeDoodles.get(i).getZ());
        }
        return light_status;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
