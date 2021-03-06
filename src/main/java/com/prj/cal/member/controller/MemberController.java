package com.prj.cal.member.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prj.cal.member.Member;
import com.prj.cal.member.service.MemberService;


@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	MemberService service;

	@ModelAttribute("cp")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath(); //cp에 cal을 넣은 것.
	}

	@ModelAttribute("serverTime")
	public String getServerTime(Locale locale) {

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		return dateFormat.format(date);
	}

	// Join
	@RequestMapping("/joinForm")
	public String joinForm(Member member) {
		return "/member/joinForm";
	}

	@RequestMapping(value = "/join", method = RequestMethod.POST)
	public String joinReg(Model model, RedirectAttributes rttr, Member member) {

		int result = service.memberRegister(member);
		if(result == 0) {
			model.addAttribute("joinError", 1);
			return "/member/joinForm";
		}
		rttr.addFlashAttribute("joinSuccess", 1);
		return "redirect:/main";
	}

	// Login
	@RequestMapping("/loginForm")
	public String loginForm(Member member) {
		return "/member/loginForm";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String memLogin(Model model, Member member, HttpSession session) {
		// @@T 노트 출력 조건 noteId == memId && noteDate == selectedDate 를 memberSearch 처럼 구현 가능할 듯.

		Member mem = service.memberSearch(member);
		if(mem == null) {//로그인 실패
			model.addAttribute("loginError", 1);
			return "/member/loginForm";
		}
		//"member"란 키에 mem을 저장했다.
		session.setAttribute("member", mem);

		//로그인 성공
		return "redirect:/main";
	}

	// Logout
	@RequestMapping("/logout")
	public String memLogout(RedirectAttributes rttr, Member member, HttpSession session) {

		session.invalidate();
		rttr.addFlashAttribute("logoutSuccess", 1);
		return "redirect:/main";
	}

	// Modify
	@RequestMapping(value = "/modifyForm")
	public ModelAndView modifyForm(HttpServletRequest request) {

		HttpSession session = request.getSession();
		Member member = (Member) session.getAttribute("member");

		ModelAndView mav = new ModelAndView();
		mav.addObject("member", service.memberSearch(member));

		mav.setViewName("/member/modifyForm");
		return mav;
	}

	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	public String modify(RedirectAttributes rttr, Member member, HttpServletRequest request) {

		HttpSession session = request.getSession();

		Member mem = service.memberModify(member);
		if(mem == null) {
			return "/member/modifyForm";
		} else {
			session.setAttribute("member", mem);
			rttr.addFlashAttribute("modifySuccess", 1);
			return "redirect:/main";
		}

	}

	// Remove
	@RequestMapping("/removeForm")
	public ModelAndView removeForm(HttpServletRequest request) {

		ModelAndView mav = new ModelAndView();

		HttpSession session =  request.getSession();
		Member member = (Member) session.getAttribute("member");

		mav.addObject("member", member);
		mav.setViewName("/member/removeForm");

		return mav;
	}

	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public String memRemove(RedirectAttributes rttr, Member member, HttpServletRequest request) {

		int result = service.memberRemove(member);

		if(result == 0) {
			rttr.addFlashAttribute("removeError", 1);
			return "/member/removeForm";
		}

		HttpSession session = request.getSession();
		session.invalidate();

		rttr.addFlashAttribute("removeSuccess", 1);
		return "redirect:/main";
	}

}
