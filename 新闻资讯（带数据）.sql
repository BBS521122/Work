/*
SQLyog Ultimate v8.71 
MySQL - 8.0.13 : Database - shixun2-2
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`shixun2-2` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `shixun2-2`;

/*Table structure for table `news` */

DROP TABLE IF EXISTS `news`;

CREATE TABLE `news` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(255) NOT NULL COMMENT '新闻标题',
  `summary` text NOT NULL COMMENT '新闻简介',
  `content` longtext NOT NULL COMMENT '新闻内容（富文本）',
  `image_path` varchar(512) NOT NULL COMMENT '新闻图片路径',
  `author` varchar(100) NOT NULL COMMENT '作者',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除（0=正常，1=删除）',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资讯表';

/*Data for the table `news` */

insert  into `news`(`id`,`title`,`summary`,`content`,`image_path`,`author`,`created_time`,`updated_time`,`is_deleted`,`sort_order`) values (10,'测试0','这是一条测试','<p><img src=\"/media/97d28071-a5fc-422e-8986-2d0beff5e76e.jpg\" alt=\"\" data-href=\"\" style=\"\"/></p>','/media/b71f2e73-cc63-432c-b5cb-f2bb2d72513c.jpg','某','2025-06-25 12:16:01','2025-06-26 17:21:08',0,1),(11,'测试1','这是一条测试','<p>.</p>','/media/1474f0b1-0189-4b87-8fa1-36adc804f7e4.jpg','某','2025-06-25 12:16:25','2025-06-26 22:42:38',0,10),(12,'测试2','这是一条测试','<p><a href=\"https://chat.deepseek.com/a/chat/s/27e8b392-8b29-4428-a826-ba3b583e0934\" target=\"_blank\">DeepSeek</a></p>','/media/5d3b3d54-74d4-4731-b3ed-b2a3034e01a4.jpg','某某','2025-06-25 12:17:04','2025-06-26 22:29:27',0,13),(13,'测试3','这是一条测试','<p>测试</p>','/media/e0b51a35-fd00-423a-aef8-6751e6e863a4.jpg','某','2025-06-26 11:27:51','2025-06-26 19:52:08',0,2),(14,'测试4','这是一条测试','<p>测试</p>','/media/c82eb0bd-4be2-463d-bfa3-10dad9ab1a45.jpg','某','2025-06-26 11:28:22','2025-06-26 19:52:08',0,4),(16,'2','2','<p>2</p>','/media/58d6b8dc-c4f2-4b48-a766-404da39f9d64.jpg','2','2025-06-26 11:29:07','2025-06-26 19:52:08',0,3),(17,'3','3','<p>3</p>','/media/136c1861-5788-4ee5-8b55-50cd76576278.jpg','3','2025-06-26 11:29:17','2025-06-26 22:05:56',0,8),(18,'4','4','<p>4.</p>','/media/9de688c9-8afe-4618-9192-632373168abd.jpg','4','2025-06-26 11:29:26','2025-06-26 20:07:51',0,5),(19,'5','5','<p>5</p>','/media/f25b7b2b-0ca3-4692-983d-918e7f2bfa0d.jpg','5','2025-06-26 12:25:42','2025-06-26 20:18:30',0,6),(20,'6','6','<p>6</p>','/media/2702562b-0284-413b-91df-fbf536e85b2d.jpg','6','2025-06-26 14:19:41','2025-06-26 15:47:02',0,0),(21,'a','a','<p>.</p>','/media/408d6530-09ae-495a-92aa-321b91520e86.jpg','a','2025-06-26 20:39:03','2025-06-26 22:05:56',0,7),(22,'b','b','<p>b</p>','/media/afd86477-d4c3-42e2-aa02-9ff64da4a0f6.jpg','b','2025-06-26 21:39:28','2025-06-26 22:29:27',0,12),(23,'c','c','<p>c</p>','/media/6e0be0db-ca83-4137-a1cc-fac4e536dafe.jpg','c','2025-06-26 22:05:19','2025-06-26 22:29:27',0,11),(24,'d','d','<p>d</p>','/media/6c4698fc-6f7d-4c82-86e4-588768184076.jpg','d','2025-06-26 22:19:02','2025-06-26 22:19:02',0,9);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
