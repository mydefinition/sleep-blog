# 可持久化层设计

## 数据库设计

### 用户(user)

| 键名           | 含义         | 类型            | 约束                               | 释义                   |
| -------------- | ------------ | --------------- | ---------------------------------- | ---------------------- |
| **id**         | **ID**       | **INTEGER**     | **PRIMARY KEY**，**AUTOINCREMENT** | 主键                   |
| **nick_name**  | 昵称         | **VARCHAR(50)** | **NOT NULL**                       |                        |
| **password**   | 密码（密文） | **CHAR(60)**    | **NOT NULL**                       | 使用 **BCrypt** 哈希后 |
| **role**       | 角色         | **VARCHAR(10)** | **NOT NULL**                       | **Role** 枚举类型      |
| **email**      | 邮箱         | **VARCHAR(50)** | **NOT NULL**，**UNIQUE**           |                        |
| **created_at** | 注册时间     | **DATETIME**    | **DEFAULT CURRENT_TIMESTAMP**      |                        |

### 分类(category)

此表用于文章 *三级* 分类.

| 键名          | 含义            | 类型            | 约束                                                      | 释义                         |
| ------------- | --------------- | --------------- | --------------------------------------------------------- | ---------------------------- |
| **id**        | **ID**          | **INTEGER**     | **PRIMARY KEY**，**AUTOINCREMENT**                        | 主键                         |
| **name**      | 分类名          | **VARCHAR(50)** | **NOT NULL**                                              |                              |
| **level1_id** | 一级分类 **ID** | **INTEGER**     | **NOT NULL**，**REFERENCES category(id)**，**DEFAULT  0** | 若本身为 *一级* 分类则为 $0$ |
| **level2_id** | 二级分类 **ID** | **INTEGER**     | **NOT NULL**，**REFERENCES category(id)**，**DEFAULT  0** | 若本身为 *二级* 分类则为 $0$ |

### 文章(article)

| 键名             | 含义        | 类型             | 约束                                        | 释义   |
| ---------------- | ----------- | ---------------- | ------------------------------------------- | ------ |
| **id**           | **ID**      | **INTEGER**      | **PRIMARY KEY**，**AUTOINCREMENT**          | 主键   |
| **title**        | 标题        | **VARCHAR(100)** | **NOT NULL**                                |        |
| **content**      | 内容        | **TEXT**         | **NOT NULL**                                |        |
| **summary**      | 摘要        | **VARCHAR(200)** | **NOT NULL**                                |        |
| **author_id**    | 作者 **ID** | **INTEGER**      | **REFERENCES user(id)**                     | 外键   |
| **is_published** | 是否发布    | **TINYINT**      | **NOT NULL**，**DEFAULT 0**                 |        |
| **is_deleted**   | 是否删除    | **TINYINT**      | **NOT NULL**，**DEFAULT 0**                 | 软删除 |
| **created_at**   | 创建时间    | **DATETIME**     | **NOT NULL**，**DEFAULT CURRENT_TIMESTAMP** |        |
| **updated_at**   | 更新时间    | **DATETIME**     |                                             |        |
| **category_id**  | 分类 **ID** | **INTEGER**      | **REFERENCES category(id)**                 | 外键   |

### 标签(tag)

| 键名     | 含义   | 类型            | 约束                               | 释义 |
| -------- | ------ | --------------- | ---------------------------------- | ---- |
| **id**   | **ID** | **INTEGER**     | **PRIMARY KEY**，**AUTOINCREMENT** | 主键 |
| **name** | 标签名 | **VARCHAR(50)** | **NOT NULL**，**UNIQUE**           |      |

### 文章-标签联系(article_tag)

| 键名           | 含义        | 类型        | 约束                                     | 释义           |
| -------------- | ----------- | ----------- | ---------------------------------------- | -------------- |
| **article_id** | 文章 **ID** | **INTEGER** | **NOT NULL**，**REFERENCES article(id)** | 外键，联合主键 |
| **tag_id**     | 标签 **ID** | **INTEGER** | **NOT NULL**，**REFERENCES tag(id)**     | 外键，联合主键 |

```sqlite
PRIMARY KEY (article_id, tag_id)
```

### 分类-标签联系(category_tag)

统计表，优化查询，未启用

| 键名            | 含义        | 类型        | 约束                                      | 释义           |
| --------------- | ----------- | ----------- | ----------------------------------------- | -------------- |
| **category_id** | 分类 **ID** | **INTEGER** | **NOT NULL**，**REFERENCES category(id)** | 外键，联合主键 |
| **tag_id**      | 标签 **ID** | **INTEGER** | **NOT NULL**，**REFERENCES tag(id)**      | 外键，联合主键 |
| **count**       | 标签计数    | **INTEGER** | **NOT NULL**                              |                |

```sqlite
PRIMARY KEY (category_id, tag_id)
```

### 评论(comment)

| 键名           | 含义        | 类型         | 约束                                        | 释义   |
| -------------- | ----------- | ------------ | ------------------------------------------- | ------ |
| **id**         | **ID**      | **INTEGER**  | **PRIMARY KEY**，**AUTOINCREMENT**          | 主键   |
| **content**    | 内容        | **TEXT**     | **NOT NULL**                                |        |
| **article_id** | 文章 **ID** | **INTEGER**  | **NOT NULL**，**REFERENCES article(id)**    | 外键   |
| **user_id**    | 用户 **ID** | **INTEGER**  | **NOT NULL**，**REFERENCES user(id)**       | 外键   |
| **is_deleted** | 软删除标记  | **TINYINT**  | **NOT NULL**，**DEFAULT 0**                 | 软删除 |
| **created_at** | 发布时间    | **DATETIME** | **NOT NULL**，**DEFAULT CURRENT_TIMESTAMP** |        |

### 文章图片(article_image)

| 键名           | 含义        | 类型            | 约束                                     | 释义 |
| -------------- | ----------- | --------------- | ---------------------------------------- | ---- |
| **id**         | **ID**      | **INTEGER**     | **PRIMARY KEY**，**AUTOINCREMENT**       | 主键 |
| **article_id** | 文章 **ID** | **INTEGER**     | **NOT NULL**，**REFERENCES article(id)** | 外键 |
| **file_name**  | 文件名      | **VARCHAR(42)** | **NOT NULL**                             |      |

### 文件存储(file_storage)

| 键名          | 含义          | 类型         | 约束                               | 释义     |
| ------------- | ------------- | ------------ | ---------------------------------- | -------- |
| **id**        | **ID**        | **INTEGER**  | **PRIMARY KEY**，**AUTOINCREMENT** | 主键     |
| **name**      | 文件名        | **TEXT**     | **NOT NULL**                       |          |
| **is_dir**    | 是否为目录    | **TINYINT**  | **NOT NULL**，**DEFAULT 0**        |          |
| **size**      | 文件大小      | **BIGINT**   |                                    | **Byte** |
| **local_id**  | 文件所处目录  | **INTEGER**  | **REFERENCES file_storage(id)**    | 自引外键 |
| **hash**      | 哈希值        | **CHAR(32)** |                                    |          |
| **user_id**   | 上传者 **ID** | **INTEGER**  |                                    |          |
| **upload_at** | 上传日期      | **DATETIME** |                                    |          |

