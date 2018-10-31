# Package
## 拼接json/text格式接口对象

### 样例代码
```javascript
jQuery.ajax(
        {
            type: "get",
            url: "/Package/Package",
            data:{
                paramHashJsonStr:JSON.stringify({
                    url:"http://ip.taobao.com/service/getIpInfo.php?ip=140.205.220.96",
                    props:[
                        {
                            url:"http://gc.ditu.aliyun.com/geocoding?a=${$parent.data.city}",
                            alias:"mapAddress",
                            condition:"($parent.code == 0?true:false)",
                            parentExp:""
                        }
                    ]
                })
            },
            dataType:'json',
            async:false,
            success: function(pdata){
            }
        }
    );
```
### 简单的使命
按照顺序层级的url访问接口
1. 访问 http://ip.taobao.com/service/getIpInfo.php?ip=140.205.220.96 并返回json对象
2. 将返回json对象带入后面计算, $parent变量的值根据parentExp来获取, 空表示json对象本身
3. $parent带入执行后, 返回的值以alias的值为属性名, 赋值给$parent对象

### 输入输出样例
输入参数
```javascript
{
    url:"http://ip.taobao.com/service/getIpInfo.php?ip=140.205.220.96",
    props:[
        {
            url:"http://gc.ditu.aliyun.com/geocoding?a=${$parent.data.city}",
            alias:"mapAddress",
            condition:"($parent.code == 0?true:false)",
            parentExp:""
        }
    ]
}
```
输出结果
```javascript
{
  "data": {
    "region": "上海",
    "area_id": "",
    "isp": "阿里巴巴",
    "country_id": "CN",
    "region_id": "310000",
    "ip": "140.205.220.96",
    "country": "中国",
    "city": "上海",
    "isp_id": "100098",
    "city_id": "310100",
    "area": "",
    "county": "XX",
    "county_id": "xx"
  },
  "code": 0,
  "mapAddress": {
    "lon": 116.40752,
    "level": -1,
    "address": "",
    "cityName": "",
    "alevel": 4,
    "lat": 39.90403
  }
}
```
