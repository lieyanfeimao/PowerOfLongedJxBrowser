/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月4日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.xuanyimao.poljb.index.JxbManager;


/**
 * @Description:
 * @author liuming
 */
public class TabCloseListener implements MouseListener{
	private int index=0;
	
	public TabCloseListener(int index) {
		this.index=index;
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("点击了label");
		JxbManager.getInstance().removeTab(null, index);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		
	}

}
