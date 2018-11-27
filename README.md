# Package
## 服务编排, 拼接json/text格式接口对象
编排基于http协议的微服务

### 服务注册逻辑
通过/Package/PatternMapping进行服务对应地址查看或注册
1. 如/Package/PatternMapping?op=register&pattern=^/.*&host=127.0.0.1:8080 进行注册
2. 如/Package/PatternMapping?op=unRegister&pattern=^/.*&host=127.0.0.1:8080 进行解绑
3. 如/Package/PatternMapping进行查看

### 简单执行流程说明

按照顺序层级的url访问接口
1. 计算$parent的值
处理上级返回对象, 根据parentExp来获取$parent变量的值, parentExp为空表示$parent为上级返回对象本身
注: 第一级$parent为空对象
2. 讲$parent带执行condition表达式, 返回true则执行
3. 讲$parent带入获取URL, 并根据服务注册情况获取完整URL
4. 执行URL返回结果根据reurnType[json, txt]处理为对象, 默认为json对象, 获取$obj
5. 讲$obj带入deafFunc执行, 返回结果赋值给$obj
6. 讲$obj以alias的值为属性名, 赋值给$parent对象

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


### 编排Ajax代码转换逻辑
/Package/ParseToScript?hostParse=1&paramHashJsonStr=