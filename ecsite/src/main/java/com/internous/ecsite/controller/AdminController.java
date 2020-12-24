package com.internous.ecsite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.internous.ecsite.model.dao.GoodsRepository;
import com.internous.ecsite.model.dao.UserRepository;
import com.internous.ecsite.model.entity.Goods;
import com.internous.ecsite.model.entity.User;
import com.internous.ecsite.model.form.GoodsForm;
import com.internous.ecsite.model.form.LoginForm;


@Controller
//URL(localhost:8080/ecsite/admin/)でアクセスできるよう設定
@RequestMapping("/ecsite/admin")
public class AdminController {
	
	@Autowired
	//loginFormから渡されるユーザ情報を条件にUserテーブルDB検索するためのDAOを読み込む
	private UserRepository userRepos;
	
	@Autowired
	//loginFormから渡されるユーザ情報を条件にGoodsテーブルDB検索するためのDAOを読み込む
	private GoodsRepository goodsRepos;
	
	//トップページ(adminindex.html)に遷移するメソッド
	@RequestMapping("/")
	public String index() {
		return "adminindex";
	}
	
	@PostMapping("/welcome")
	public String welcome(LoginForm form,Model m) {
		//ユーザ名とパスワードでユーザを詮索
		List<User> users = userRepos.findByUserNameAndPassword(form.getUserName(),form.getPassword());
		
		//検索結果が存在していれば、isAdmin(管理者かどうか)を取得し、管理者だった場合のみ処理
		if(users !=null && users.size() > 0) {
			boolean isAdmin = users.get(0).getIsAdmin() !=0;
			if(isAdmin) {
				List<Goods> goods = goodsRepos.findAll();
				m.addAttribute("userName",users.get(0).getUserName());
				m.addAttribute("password",users.get(0).getPassword());
				m.addAttribute("goods",goods);
			}
		}
		
		return "welcome";
		
	}
	@RequestMapping("/goodsMst")
	public String goodsMst(LoginForm form,Model m) {
		m.addAttribute("userName",form.getUserName());
		m.addAttribute("password",form.getPassword());
		
		return "goodsmst";
	}
	
	@RequestMapping("/addGoods")
	public String addGoods(GoodsForm goodsForm,LoginForm loginForm,Model m) {
		m.addAttribute("userName",loginForm.getUserName());
		m.addAttribute("password",loginForm.getPassword());
		
		Goods goods = new Goods();
		goods.setGoodsName(goodsForm.getGoodsName());
		goods.setPrice(goodsForm.getPrice());
		goodsRepos.saveAndFlush(goods);
		
		return "forward:/ecsite/admin/welcome";
	}
	
	@ResponseBody
	@PostMapping("/api/deleteGoods")
	public String deleteApi(@RequestBody GoodsForm f,Model m) {
		try {
			goodsRepos.deleteById(f.getId());
		} catch (IllegalArgumentException e) {
			return "-1";
		}
		return "1";
	}

}
