package com.hs.mail.webmail.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hs.mail.webmail.WmaSession;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaMessageInfoList;
import com.hs.mail.webmail.model.WmaStore;
import com.hs.mail.webmail.search.Query;
import com.hs.mail.webmail.util.Pager;
import com.hs.mail.webmail.util.RequestUtils;

@Controller
@RequestMapping("/folder")
public class FolderController {

	@RequestMapping(value = "display")
	public ModelAndView display(HttpSession httpsession,
			HttpServletRequest request) throws ServletException, WmaException {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		int page = RequestUtils.getParameterInt(request, "page", 0);
		int size = RequestUtils.getParameterInt(request, "size", 15);
		boolean init = RequestUtils.getParameterBool(request, "init", false);
		Query query = session.getQuery(init ? request : null);
		return doDisplayFolder(session, path, page, size, query);
	}
	
	private ModelAndView doDisplayFolder(WmaSession session, String path,
			int page, int size, Query query) throws WmaException {
		WmaStore store = session.getWmaStore();
		Pager pager = new Pager(page, size, query.isAscending());
		WmaMessageInfoList msglist = WmaMessageInfoList
				.createWmaMessageInfoList(store.getFolder(path),
						query.getSearchTerm(), query.getSortTerm(), pager);
		ModelAndView mav = new ModelAndView("messagelist");
		mav.addObject("messages", msglist.getMessageInfos());
		mav.addObject("pager", pager);
		return mav;
	}

}
