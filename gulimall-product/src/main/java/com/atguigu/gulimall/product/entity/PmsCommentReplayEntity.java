package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * ??Ʒ???ۻظ???ϵ
 * 
 * @author thehou
 * @email thehou555@gmail.com
 * @date 2024-10-11 12:39:02
 */
@Data
@TableName("pms_comment_replay")
public class PmsCommentReplayEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * ????id
	 */
	private Long commentId;
	/**
	 * ?ظ?id
	 */
	private Long replyId;

}
