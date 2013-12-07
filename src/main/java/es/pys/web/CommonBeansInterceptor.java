package es.pys.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import es.pys.model.Sesion;
import es.pys.model.Usuario;

public class CommonBeansInterceptor implements HandlerInterceptor {

	@Autowired
	private Sesion sesion;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// Insertamos otros beans que son propios de las vistas
		if (modelAndView != null)
			modelAndView.getModelMap().addAttribute("usuario",
					new Usuario(sesion.getId(), sesion.getNombre(), sesion.getPermisos()));
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}
}
