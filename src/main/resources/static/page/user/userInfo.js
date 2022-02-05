var form, $, areaData;
layui.config({
	base: "../../js/"
}).extend({
	"address": "address"
})
layui.use(['form', 'layer', 'upload', 'laydate', "address"], function() {
	form = layui.form;
	$ = layui.jquery;
	var layer = parent.layer === undefined ? layui.layer : top.layer,
		upload = layui.upload,
		laydate = layui.laydate,
		address = layui.address;

	//上传头像
	upload.render({
		elem: '#userFace',
		url: '../../json/userface.json',
		method: "get", //此处是为了演示之用，实际使用中请将此删除，默认用post方式提交
		before: function(obj) {
			//预读本地文件示例，不支持ie8
			obj.preview(function(index, file, result) {
				$('#userFace').attr('src', result);
				window.sessionStorage.setItem('userFace', result);
			});
		},
		done: function(res, index, upload) {


		}
	});


	//添加验证规则
	form.verify({
		userBirthday: function(value) {
			if (!
				/^(\d{4})[\u4e00-\u9fa5]|[-\/](\d{1}|0\d{1}|1[0-2])([\u4e00-\u9fa5]|[-\/](\d{1}|0\d{1}|[1-2][0-9]|3[0-1]))*$/.test(
					value)) {
				return "出生日期格式不正确！";
			}
		}
	})
	//选择出生日期
	laydate.render({
		elem: '.userBirthday',
		format: 'yyyy年MM月dd日',
		trigger: 'click',
		max: 0,
		mark: {
			"0-12-15": "生日"
		},
		done: function(value, date) {
			// if(date.month === 12 && date.date === 15){ //点击每年12月15日，弹出提示语
			//     layer.msg('今天是马哥的生日，也是layuicms2.0的发布日，快来送上祝福吧！');
			// }
		}
	});

	//获取省信息
	address.provinces();

	//提交个人资料
	form.on("submit(changeUser)", function(data) {
		var index = layer.msg('提交中，请稍候', {
			icon: 16,
			time: false,
			shade: 0.8
		});
		//将填写的用户信息存到session以便下次调取
		var key, userInfoHtml = '';
		userInfoHtml = {
			'realName': $(".realName").val(),
			'sex': data.field.sex,
			'userPhone': $(".userPhone").val(),
			'userBirthday': $(".userBirthday").val(),
			'province': data.field.province,
			'city': data.field.city,
			'area': data.field.area,
			'userEmail': $(".userEmail").val(),
			'myself': $(".myself").val()
		};
		for (key in data.field) {
			if (key.indexOf("like") != -1) {
				userInfoHtml[key] = "on";
			}
		}
		window.sessionStorage.setItem("userInfo", JSON.stringify(userInfoHtml));
		setTimeout(function() {
			layer.close(index);
			layer.msg("提交成功！");

		}, 2000);
		return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
	});



	//判断是否设置过头像，如果设置过则修改顶部、左侧和个人资料中的头像，否则使用默认头像
	if (window.sessionStorage.getItem('userFace') && $(".userAvatar").length > 0) {
		var avatar = window.sessionStorage.getItem('userFace')
		$("#userFace").attr("src", avatar);
		$(".userAvatar").attr("src", avatar);
	} else {
		$("#userFace").attr("src", "../../images/face.jpg");
	}

	//判断是否修改过用户信息，如果修改过则填充修改后的信息
	var menuText = $("#top_tabs", parent.document).text(); //判断打开的窗口是否存在“个人资料”页面
	var citys, areas;
	if (window.sessionStorage.getItem('userInfo')) {
		//获取省信息
		address.provinces();
		var userInfo = JSON.parse(window.sessionStorage.getItem('userInfo'));
		var citys;
		$(".realName").val(userInfo.realName); //用户名
		$(".userSex input[value=" + userInfo.sex + "]").attr("checked", "checked"); //性别
		$(".userPhone").val(userInfo.userPhone); //手机号
		$(".userBirthday").val(userInfo.userBirthday); //出生年月
		//填充省份信息，同时调取市级信息列表
		$.get("../../json/address.json", function(addressData) {
			$(".userAddress select[name='province']").val(userInfo.province); //省
			var value = userInfo.province;
			if (value > 0) {
				address.citys(addressData[$(".userAddress select[name='province'] option[value='" + userInfo.province + "']").index() -
					1].childs);
				citys = addressData[$(".userAddress select[name='province'] option[value='" + userInfo.province + "']").index() -
					1].childs;
			} else {
				$('.userAddress select[name=city]').attr("disabled", "disabled");
			}
			$(".userAddress select[name='city']").val(userInfo.city); //市
			//填充市级信息，同时调取区县信息列表
			var value = userInfo.city;
			if (value > 0) {
				address.areas(citys[$(".userAddress select[name=city] option[value='" + userInfo.city + "']").index() - 1].childs);
			} else {
				$('.userAddress select[name=area]').attr("disabled", "disabled");
			}
			$(".userAddress select[name='area']").val(userInfo.area); //区
			form.render();
		})
		for (key in userInfo) {
			if (key.indexOf("like") != -1) {
				$(".userHobby input[name='" + key + "']").attr("checked", "checked");
			}
		}
		$(".userEmail").val(userInfo.userEmail); //用户邮箱
		$(".myself").val(userInfo.myself); //自我评价
		form.render();
	};


	//修改密码
	form.on("submit(changePwd)", function(data) {
		var index = layer.msg('提交中，请稍候', {
			icon: 16,
			time: false,
			shade: 0.8
		});
		setTimeout(function() {
			layer.close(index);
			layer.msg("密码修改成功！");
			$(".pwd").val('');
		}, 2000);
		return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
	})
})
