package com.wya.env.module.register;

import android.widget.TextView;

import com.wya.env.R;
import com.wya.env.base.BaseActivity;

/**
 * @date: 2020/8/1 15:48
 * @author: Chunjiang Mao
 * @classname: RegisterProtocolActivity
 * @describe: 注册协议
 */
public class RegisterProtocolActivity extends BaseActivity {

    private int type;
    @Override
    protected void initView() {
        TextView content = (TextView) findViewById(R.id.content);
        type = getIntent().getIntExtra("type", 0);
        if(type == 1){
            setTitle("TERMS & CONDITIONS");
            content.setText("Shenzhen Connate Photoelectricity Technology Co., Ltd. informs the User that the Services are offered exclusively to persons at least 18 years old.\n" +
                    "\n" +
                    "Unless otherwise stated, all rights of whatever nature relating to the App are the exclusive property of Connate. All copyrights and any other intellectual or industrial property rights or any other rights of any nature relating to the App, to its contents, to the service provided with it or in any other way connected to it, are considered reserved and protected by china, and international laws on intellectual and / or industrial property. Therefore, the violation of the related rights can lead to the application of criminal, civil or administrative sanctions envisaged by the reference standards.\n" +
                    "\n" +
                    "In accordance with current regulatory provisions, the User is granted a limited, revocable, non-exclusive, non-transferrable and non-sublicensable license, and the right to download, to install and to use the App, and to view and store on his device (at his discretion, under his responsibility and where permitted by the application itself) the information and the data conveyed with it. The User can download, install and use the App on any authorized device of his property, or in his legitimate availability, exclusively for the uses permitted by the license, within the limits of the functionality immediately and directly made available by the application itself, and under his own responsibility the User can back up the data obtained through the application.\n" +
                    "In any case, the User undertakes to use the App exclusively in compliance with and within the limits set by the Terms and Conditions and, in any case, always in compliance with the current legal provisions.\n");
        } else if(type == 2){
            setTitle("Privacy Policy");
            content.setText("Privacy Policy\n" +
                    "Last updated: January 11, 2021\n" +
                    "This Privacy Policy describes Our policies and procedures on the collection, use and disclosure of Your information when You use the Service and tells You about Your privacy rights and how the law protects You.\n" +
                    "We use Your Personal data to provide and improve the Service. By using the Service, You agree to the collection and use of information in accordance with this Privacy Policy. This Privacy Policy has been created with the help of the Privacy Policy Generator.\n" +
                    "Interpretation and Definitions\n" +
                    "Interpretation\n" +
                    "The words of which the initial letter is capitalized have meanings defined under the following conditions. The following definitions shall have the same meaning regardless of whether they appear in singular or in plural.\n" +
                    "Definitions\n" +
                    "For the purposes of this Privacy Policy:\n" +
                    "Account means a unique account created for You to access our Service or parts of our Service.\n" +
                    "Affiliate means an entity that controls, is controlled by or is under common control with a party, where \"control\" means ownership of 50% or more of the shares, equity interest or other securities entitled to vote for election of directors or other managing authority.\n" +
                    "Application means the software program provided by the Company downloaded by You on any electronic device, named Deluxlight\n" +
                    "Company (referred to as either \"the Company\", \"We\", \"Us\" or \"Our\" in this Agreement) refers to Shenzhen Connate Photoelectricity Technology Co., Ltd., 401,A5 Building, No. 168 Changshang Industrial,Liulian,Pingdi Street,Longgang District, Shenzhen,China..\n" +
                    "Country refers to: China\n" +
                    "Device means any device that can access the Service such as a computer, a cellphone or a digital tablet.\n" +
                    "Personal Data is any information that relates to an identified or identifiable individual.\n" +
                    "Service refers to the Application.\n" +
                    "Service Provider means any natural or legal person who processes the data on behalf of the Company. It refers to third-party companies or individuals employed by the Company to facilitate the Service, to provide the Service on behalf of the Company, to perform services related to the Service or to assist the Company in analyzing how the Service is used.\n" +
                    "Third-party Social Media Service refers to any website or any social network website through which a User can log in or create an account to use the Service.\n" +
                    "Usage Data refers to data collected automatically, either generated by the use of the Service or from the Service infrastructure itself (for example, the duration of a page visit).\n" +
                    "You means the individual accessing or using the Service, or the company, or other legal entity on behalf of which such individual is accessing or using the Service, as applicable.\n" +
                    "Collecting and Using Your Personal Data\n" +
                    "Types of Data Collected\n" +
                    "Personal Data\n" +
                    "While using Our Service, We may ask You to provide Us with certain personally identifiable information that can be used to contact or identify You. Personally identifiable information may include, but is not limited to:\n" +
                    "Email address\n" +
                    "Usage Data\n" +
                    "Usage Data\n" +
                    "Usage Data is collected automatically when using the Service.\n" +
                    "Usage Data may include information such as Your Device's Internet Protocol address (e.g. IP address), browser type, browser version, the pages of our Service that You visit, the time and date of Your visit, the time spent on those pages, unique device identifiers and other diagnostic data.\n" +
                    "When You access the Service by or through a mobile device, We may collect certain information automatically, including, but not limited to, the type of mobile device You use, Your mobile device unique ID, the IP address of Your mobile device, Your mobile operating system, the type of mobile Internet browser You use, unique device identifiers and other diagnostic data.\n" +
                    "We may also collect information that Your browser sends whenever You visit our Service or when You access the Service by or through a mobile device.\n" +
                    "Information from Third-Party Social Media Services\n" +
                    "The Company allows You to create an account and log in to use the Service through the following Third-party Social Media Services:\n" +
                    "•\tGoogle\n" +
                    "•\tFacebook\n" +
                    "•\tTwitter\n" +
                    "If You decide to register through or otherwise grant us access to a Third-Party Social Media Service, We may collect Personal data that is already associated with Your Third-Party Social Media Service's account, such as Your name, Your email address, Your activities or Your contact list associated with that account.\n" +
                    "You may also have the option of sharing additional information with the Company through Your Third-Party Social Media Service's account. If You choose to provide such information and Personal Data, during registration or otherwise, You are giving the Company permission to use, share, and store it in a manner consistent with this Privacy Policy.\n" +
                    "Information Collected while Using the Application\n" +
                    "While using Our Application, in order to provide features of Our Application, We may collect, with Your prior permission:\n" +
                    "•\tInformation regarding your location\n" +
                    "We use this information to provide features of Our Service, to improve and customize Our Service. The information may be uploaded to the Company's servers and/or a Service Provider's server or it may be simply stored on Your device.\n" +
                    "You can enable or disable access to this information at any time, through Your Device settings.\n" +
                    "Use of Your Personal Data\n" +
                    "The Company may use Personal Data for the following purposes:\n" +
                    "To provide and maintain our Service, including to monitor the usage of our Service.\n" +
                    "To manage Your Account: to manage Your registration as a user of the Service. The Personal Data You provide can give You access to different functionalities of the Service that are available to You as a registered user.\n" +
                    "For the performance of a contract: the development, compliance and undertaking of the purchase contract for the products, items or services You have purchased or of any other contract with Us through the Service.\n" +
                    "To contact You: To contact You by email, telephone calls, SMS, or other equivalent forms of electronic communication, such as a mobile application's push notifications regarding updates or informative communications related to the functionalities, products or contracted services, including the security updates, when necessary or reasonable for their implementation.\n" +
                    "To provide You with news, special offers and general information about other goods, services and events which we offer that are similar to those that you have already purchased or enquired about unless You have opted not to receive such information.\n" +
                    "To manage Your requests: To attend and manage Your requests to Us.\n" +
                    "For business transfers: We may use Your information to evaluate or conduct a merger, divestiture, restructuring, reorganization, dissolution, or other sale or transfer of some or all of Our assets, whether as a going concern or as part of bankruptcy, liquidation, or similar proceeding, in which Personal Data held by Us about our Service users is among the assets transferred.\n" +
                    "For other purposes: We may use Your information for other purposes, such as data analysis, identifying usage trends, determining the effectiveness of our promotional campaigns and to evaluate and improve our Service, products, services, marketing and your experience.\n" +
                    "We may share Your personal information in the following situations:\n" +
                    "•\tWith Service Providers: We may share Your personal information with Service Providers to monitor and analyze the use of our Service, to contact You.\n" +
                    "•\tFor business transfers: We may share or transfer Your personal information in connection with, or during negotiations of, any merger, sale of Company assets, financing, or acquisition of all or a portion of Our business to another company.\n" +
                    "•\tWith Affiliates: We may share Your information with Our affiliates, in which case we will require those affiliates to honor this Privacy Policy. Affiliates include Our parent company and any other subsidiaries, joint venture partners or other companies that We control or that are under common control with Us.\n" +
                    "•\tWith business partners: We may share Your information with Our business partners to offer You certain products, services or promotions.\n" +
                    "•\tWith other users: when You share personal information or otherwise interact in the public areas with other users, such information may be viewed by all users and may be publicly distributed outside. If You interact with other users or register through a Third-Party Social Media Service, Your contacts on the Third-Party Social Media Service may see Your name, profile, pictures and description of Your activity. Similarly, other users will be able to view descriptions of Your activity, communicate with You and view Your profile.\n" +
                    "•\tWith Your consent: We may disclose Your personal information for any other purpose with Your consent.\n" +
                    "Retention of Your Personal Data\n" +
                    "The Company will retain Your Personal Data only for as long as is necessary for the purposes set out in this Privacy Policy. We will retain and use Your Personal Data to the extent necessary to comply with our legal obligations (for example, if we are required to retain your data to comply with applicable laws), resolve disputes, and enforce our legal agreements and policies.\n" +
                    "The Company will also retain Usage Data for internal analysis purposes. Usage Data is generally retained for a shorter period of time, except when this data is used to strengthen the security or to improve the functionality of Our Service, or We are legally obligated to retain this data for longer time periods.\n" +
                    "Transfer of Your Personal Data\n" +
                    "Your information, including Personal Data, is processed at the Company's operating offices and in any other places where the parties involved in the processing are located. It means that this information may be transferred to — and maintained on — computers located outside of Your state, province, country or other governmental jurisdiction where the data protection laws may differ than those from Your jurisdiction.\n" +
                    "Your consent to this Privacy Policy followed by Your submission of such information represents Your agreement to that transfer.\n" +
                    "The Company will take all steps reasonably necessary to ensure that Your data is treated securely and in accordance with this Privacy Policy and no transfer of Your Personal Data will take place to an organization or a country unless there are adequate controls in place including the security of Your data and other personal information.\n" +
                    "Disclosure of Your Personal Data\n" +
                    "Business Transactions\n" +
                    "If the Company is involved in a merger, acquisition or asset sale, Your Personal Data may be transferred. We will provide notice before Your Personal Data is transferred and becomes subject to a different Privacy Policy.\n" +
                    "Law enforcement\n" +
                    "Under certain circumstances, the Company may be required to disclose Your Personal Data if required to do so by law or in response to valid requests by public authorities (e.g. a court or a government agency).\n" +
                    "Other legal requirements\n" +
                    "The Company may disclose Your Personal Data in the good faith belief that such action is necessary to:\n" +
                    "•\tComply with a legal obligation\n" +
                    "•\tProtect and defend the rights or property of the Company\n" +
                    "•\tPrevent or investigate possible wrongdoing in connection with the Service\n" +
                    "•\tProtect the personal safety of Users of the Service or the public\n" +
                    "•\tProtect against legal liability\n" +
                    "Security of Your Personal Data\n" +
                    "The security of Your Personal Data is important to Us, but remember that no method of transmission over the Internet, or method of electronic storage is 100% secure. While We strive to use commercially acceptable means to protect Your Personal Data, We cannot guarantee its absolute security.\n" +
                    "Detailed Information on the Processing of Your Personal Data\n" +
                    "The Service Providers We use may have access to Your Personal Data. These third-party vendors collect, store, use, process and transfer information about Your activity on Our Service in accordance with their Privacy Policies.\n" +
                    "Usage, Performance and Miscellaneous\n" +
                    "We may use third-party Service Providers to provide better improvement of our Service.\n" +
                    "Google Places\n" +
                    "Google Places is a service that returns information about places using HTTP requests. It is operated by Google\n" +
                    "Google Places service may collect information from You and from Your Device for security purposes.\n" +
                    "The information gathered by Google Places is held in accordance with the Privacy Policy of Google: https://www.google.com/intl/en/policies/privacy/\n" +
                    "Links to Other Websites\n" +
                    "Our Service may contain links to other websites that are not operated by Us. If You click on a third party link, You will be directed to that third party's site. We strongly advise You to review the Privacy Policy of every site You visit.\n" +
                    "We have no control over and assume no responsibility for the content, privacy policies or practices of any third party sites or services.\n" +
                    "Changes to this Privacy Policy\n" +
                    "We may update Our Privacy Policy from time to time. We will notify You of any changes by posting the new Privacy Policy on this page.\n" +
                    "We will let You know via email and/or a prominent notice on Our Service, prior to the change becoming effective and update the \"Last updated\" date at the top of this Privacy Policy.\n" +
                    "You are advised to review this Privacy Policy periodically for any changes. Changes to this Privacy Policy are effective when they are posted on this page.\n" +
                    "Contact Us\n" +
                    "If you have any questions about this Privacy Policy, You can contact us:\n" +
                    "•\tBy email: delightluxmgt@gmail.com\n");
        }




    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_register_protocol;
    }
}