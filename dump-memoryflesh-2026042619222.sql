-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: memoryflesh
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `estado`
--

DROP TABLE IF EXISTS `estado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `estado` (
  `idEstado` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(45) NOT NULL,
  PRIMARY KEY (`idEstado`),
  UNIQUE KEY `idEstado_UNIQUE` (`idEstado`),
  UNIQUE KEY `descripcion_UNIQUE` (`descripcion`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estado`
--

LOCK TABLES `estado` WRITE;
/*!40000 ALTER TABLE `estado` DISABLE KEYS */;
INSERT INTO `estado` VALUES (3,'Eliminado'),(2,'Privado'),(1,'Publico');
/*!40000 ALTER TABLE `estado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log`
--

DROP TABLE IF EXISTS `log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `log` (
  `idLog` int NOT NULL AUTO_INCREMENT,
  `fecha_hr` datetime NOT NULL,
  `idUsuario` int DEFAULT NULL,
  `idProcedure` int DEFAULT NULL,
  `detalle` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`idLog`),
  UNIQUE KEY `idLog_UNIQUE` (`idLog`),
  KEY `fk_Log_usr_idx` (`idUsuario`),
  KEY `fk_Log_proc_idx` (`idProcedure`),
  CONSTRAINT `fk_Log_proc` FOREIGN KEY (`idProcedure`) REFERENCES `procedimiento` (`idProcedimiento`),
  CONSTRAINT `fk_Log_usr` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log`
--

LOCK TABLES `log` WRITE;
/*!40000 ALTER TABLE `log` DISABLE KEYS */;
INSERT INTO `log` VALUES (1,'2026-04-26 18:38:05',4,1,'Marcos ha iniciado sesión'),(2,'2026-04-26 18:48:10',1,1,'Marecos321 ha iniciado sesión'),(3,'2026-04-26 18:48:32',8,2,'Pablo ha creado una cuenta con el correo: pablo@gmail.com'),(4,'2026-04-26 18:48:42',8,1,'Pablo ha iniciado sesión'),(5,'2026-04-26 18:50:25',8,3,'Pablo ha subido una memoria. Titulo = CHAAAAAAAAAAAAT SOY PABLO, Descripcion = PABLO EPICO'),(6,'2026-04-26 18:50:45',1,4,'Marecos321 ha eliminado la memoria: Titulo = CHAAAAAAAAAAAAT SOY PABLO'),(7,'2026-04-26 18:52:34',8,5,'Pablo ha cambiado su contraseña'),(8,'2026-04-26 18:52:49',8,1,'Pablo ha iniciado sesión'),(9,'2026-04-26 18:53:51',8,3,'Pablo ha subido una memoria. Titulo = CAHJSHDHSAHDA, Descripcion = ASDJSAJDSAJD'),(10,'2026-04-26 18:53:59',8,4,'Pablo ha eliminado la memoria: Titulo = CAHJSHDHSAHDA'),(17,'2026-04-26 19:14:40',6,1,'Joaquin ha iniciado sesión'),(18,'2026-04-26 19:15:13',6,3,'Joaquin ha subido una memoria. Titulo = VALORANT MVP, Descripcion = Soy muy bueno'),(19,'2026-04-26 19:15:56',6,3,'Joaquin ha subido una memoria. Titulo = Yo inteligente, Descripcion = hola soy'),(20,'2026-04-26 19:16:25',1,1,'Marecos321 ha iniciado sesión'),(21,'2026-04-26 19:17:55',1,3,'Marecos321 ha subido una memoria. Titulo = Finde de deepwork, Descripcion = socrates en su mejor expresion'),(22,'2026-04-26 19:19:18',1,3,'Marecos321 ha subido una memoria. Titulo = WII, Descripcion = sims pero menos epico'),(23,'2026-04-26 19:20:01',3,1,'itss_MaiMai ha iniciado sesión'),(24,'2026-04-26 19:20:48',3,3,'itss_MaiMai ha subido una memoria. Titulo = Estaba barato, Descripcion = baratisimo juegazo');
/*!40000 ALTER TABLE `log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `memoria`
--

DROP TABLE IF EXISTS `memoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `memoria` (
  `idMemoria` int NOT NULL AUTO_INCREMENT,
  `fecha_hr` datetime NOT NULL,
  `titulo` varchar(45) NOT NULL,
  `contenido` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `descripcion` varchar(110) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `idUsuario` int DEFAULT NULL,
  `idEstado` int DEFAULT NULL,
  PRIMARY KEY (`idMemoria`),
  UNIQUE KEY `idMemoria_UNIQUE` (`idMemoria`),
  KEY `fk_Memoria_usr_idx` (`idUsuario`),
  KEY `fk_Memoria_est_idx` (`idEstado`),
  CONSTRAINT `fk_Memoria_est` FOREIGN KEY (`idEstado`) REFERENCES `estado` (`idEstado`),
  CONSTRAINT `fk_Memoria_usr` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `memoria`
--

LOCK TABLES `memoria` WRITE;
/*!40000 ALTER TABLE `memoria` DISABLE KEYS */;
INSERT INTO `memoria` VALUES (14,'2026-04-26 19:15:13','VALORANT MVP','img_1777241713350.png','Soy muy bueno',6,1),(15,'2026-04-26 19:15:56','Yo inteligente','img_1777241756952.png','hola soy',6,2),(16,'2026-04-26 19:17:55','Finde de deepwork','img_1777241875011.png','socrates en su mejor expresion',1,1),(17,'2026-04-26 19:19:18','WII','img_1777241958924.png','sims pero menos epico',1,1),(18,'2026-04-26 19:20:48','Estaba barato','img_1777242048926.png','baratisimo juegazo',3,1);
/*!40000 ALTER TABLE `memoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `procedimiento`
--

DROP TABLE IF EXISTS `procedimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `procedimiento` (
  `idProcedimiento` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(45) NOT NULL,
  PRIMARY KEY (`idProcedimiento`),
  UNIQUE KEY `idProcedimiento_UNIQUE` (`idProcedimiento`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `procedimiento`
--

LOCK TABLES `procedimiento` WRITE;
/*!40000 ALTER TABLE `procedimiento` DISABLE KEYS */;
INSERT INTO `procedimiento` VALUES (1,'Iniciar Sesión'),(2,'Crear Cuenta'),(3,'Crear Memoria'),(4,'Dar de Baja Memoria'),(5,'Cambiar Contraseña'),(6,'Eliminar Cuenta');
/*!40000 ALTER TABLE `procedimiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `idRol` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(45) NOT NULL,
  PRIMARY KEY (`idRol`),
  UNIQUE KEY `idRol_UNIQUE` (`idRol`),
  UNIQUE KEY `descripcion_UNIQUE` (`descripcion`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
INSERT INTO `rol` VALUES (1,'Administrador'),(2,'Usuario');
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servidor`
--

DROP TABLE IF EXISTS `servidor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servidor` (
  `idServidor` int NOT NULL AUTO_INCREMENT,
  `puerto` varchar(45) NOT NULL,
  PRIMARY KEY (`idServidor`),
  UNIQUE KEY `idServidor_UNIQUE` (`idServidor`),
  UNIQUE KEY `puerto_UNIQUE` (`puerto`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servidor`
--

LOCK TABLES `servidor` WRITE;
/*!40000 ALTER TABLE `servidor` DISABLE KEYS */;
INSERT INTO `servidor` VALUES (1,'3306');
/*!40000 ALTER TABLE `servidor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `idUsuario` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) NOT NULL,
  `mail` varchar(45) NOT NULL,
  `contrasena` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `idRol` int DEFAULT NULL,
  `idServidor` int DEFAULT NULL,
  PRIMARY KEY (`idUsuario`),
  UNIQUE KEY `idUsuario_UNIQUE` (`idUsuario`),
  UNIQUE KEY `nombre_UNIQUE` (`nombre`),
  UNIQUE KEY `mail_UNIQUE` (`mail`),
  KEY `fk_Usuario_rol_idx` (`idRol`),
  KEY `fk_Usuario_serv_idx` (`idServidor`),
  CONSTRAINT `fk_Usuario_rol` FOREIGN KEY (`idRol`) REFERENCES `rol` (`idRol`),
  CONSTRAINT `fk_Usuario_serv` FOREIGN KEY (`idServidor`) REFERENCES `servidor` (`idServidor`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Marecos321','mmmarecos@gmail.com','marecos32@',1,1),(3,'itss_MaiMai','maimai@gmail.com','MaiMai321@',2,1),(4,'Marcos','marcos@gmail.com','Clave123@',2,1),(6,'Joaquin','requejo.joaquin.c@gmail.com','Joaco2122007.',2,1),(8,'Pablo','pablo@gmail.com','asd.',2,1);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'memoryflesh'
--
/*!50003 DROP PROCEDURE IF EXISTS `sp_BuscarPerfiles` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_BuscarPerfiles`(
    IN p_busqueda VARCHAR(45)
)
BEGIN
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SELECT 'Error en la búsqueda' AS mensaje;
    END;
    
    
    SELECT 
        idUsuario,
        nombre,
        mail,
        idRol
    FROM usuario
    WHERE nombre LIKE CONCAT('%', p_busqueda, '%')
    ORDER BY nombre ASC
    LIMIT 20;
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_CambiarContrasena` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_CambiarContrasena`(
    IN p_idUsuario INT,
    IN p_contrasenaActual VARCHAR(45),
    IN p_contrasenaNueva VARCHAR(45)
)
BEGIN
    DECLARE v_contrasenaDB VARCHAR(45);
    DECLARE v_nombreUsuario VARCHAR(45);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'Error al cambiar la contraseña' AS mensaje;
    END;

    START TRANSACTION;

    SELECT contrasena, nombre INTO v_contrasenaDB, v_nombreUsuario
    FROM usuario
    WHERE idUsuario = p_idUsuario
    FOR UPDATE;

    IF v_contrasenaDB IS NULL THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Usuario no encontrado';

    ELSEIF v_contrasenaDB != p_contrasenaActual THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = 'Contraseña actual incorrecta';

    ELSE
        UPDATE usuario
        SET contrasena = p_contrasenaNueva
        WHERE idUsuario = p_idUsuario;

        CALL sp_RegistrarLog(
            p_idUsuario,
            'Cambiar Contraseña',
            CONCAT(v_nombreUsuario, ' ha cambiado su contraseña')
        );

        COMMIT;

        SELECT 'Contraseña cambiada con éxito' AS mensaje;
    END IF;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_CrearCuenta` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_CrearCuenta`(
    IN p_nombre VARCHAR(45),
    IN p_mail VARCHAR(45),
    IN p_contrasena VARCHAR(45)
)
BEGIN
    DECLARE v_idRolUsuario INT DEFAULT 2;
    DECLARE v_idServidor INT DEFAULT 1;
    DECLARE v_nuevoIdUsuario INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'Error al crear la cuenta' AS mensaje, 0 AS idUsuario;
    END;

    START TRANSACTION;

    IF EXISTS (SELECT 1 FROM usuario WHERE nombre = p_nombre) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El nombre de usuario ya existe';

    ELSEIF EXISTS (SELECT 1 FROM usuario WHERE mail = p_mail) THEN
        SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = 'El correo electrónico ya está registrado';

    ELSE
        INSERT INTO usuario (nombre, mail, contrasena, idRol, idServidor)
        VALUES (p_nombre, p_mail, p_contrasena, v_idRolUsuario, v_idServidor);

        SET v_nuevoIdUsuario = LAST_INSERT_ID();

        CALL sp_RegistrarLog(
            v_nuevoIdUsuario,
            'Crear Cuenta',
            CONCAT(p_nombre, ' ha creado una cuenta con el correo: ', p_mail)
        );

        COMMIT;

        SELECT 'Cuenta creada exitosamente' AS mensaje, v_nuevoIdUsuario AS idUsuario;
    END IF;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_CrearMemoria` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_CrearMemoria`(
    IN p_titulo VARCHAR(45),
    IN p_contenido VARCHAR(45),
    IN p_descripcion VARCHAR(45),
    IN p_idUsuario INT,
    IN p_esPublica BOOLEAN
)
BEGIN
    DECLARE v_idEstado INT;
    DECLARE v_nuevaMemoriaId INT;
    DECLARE v_nombreUsuario VARCHAR(45);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'Error al crear la memoria' AS mensaje, 0 AS idMemoria;
    END;

    START TRANSACTION;

    SELECT nombre INTO v_nombreUsuario FROM usuario WHERE idUsuario = p_idUsuario;

    IF p_esPublica THEN
        SET v_idEstado = 1;
    ELSE
        SET v_idEstado = 2;
    END IF;

    INSERT INTO memoria (fecha_hr, titulo, contenido, descripcion, idUsuario, idEstado)
    VALUES (NOW(), p_titulo, p_contenido, p_descripcion, p_idUsuario, v_idEstado);

    SET v_nuevaMemoriaId = LAST_INSERT_ID();

    CALL sp_RegistrarLog(
        p_idUsuario,
        'Crear Memoria',
        CONCAT(v_nombreUsuario, ' ha subido una memoria. Titulo = ', p_titulo,
            IF(p_descripcion IS NOT NULL AND p_descripcion != '',
               CONCAT(', Descripcion = ', p_descripcion), ''))
    );

    COMMIT;

    SELECT 'Memoria creada exitosamente' AS mensaje, v_nuevaMemoriaId AS idMemoria;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_DarDeBajaMemoria` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_DarDeBajaMemoria`(
    IN p_idMemoria INT,
    IN p_idUsuario INT
)
BEGIN
    DECLARE v_idUsuarioDueno INT;
    DECLARE v_idRolUsuario INT;
    DECLARE v_tituloMemoria VARCHAR(45);
    DECLARE v_nombreUsuario VARCHAR(45);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'Error al dar de baja la memoria' AS mensaje;
    END;

    START TRANSACTION;

    SELECT idUsuario, titulo INTO v_idUsuarioDueno, v_tituloMemoria
    FROM memoria
    WHERE idMemoria = p_idMemoria
    FOR UPDATE;

    IF v_idUsuarioDueno IS NULL THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La memoria no existe';
    END IF;

    SELECT idRol, nombre INTO v_idRolUsuario, v_nombreUsuario
    FROM usuario
    WHERE idUsuario = p_idUsuario;

    IF v_idUsuarioDueno = p_idUsuario OR v_idRolUsuario = 1 THEN
        UPDATE memoria
        SET idEstado = 3
        WHERE idMemoria = p_idMemoria;

        CALL sp_RegistrarLog(
            p_idUsuario,
            'Dar de Baja Memoria',
            CONCAT(v_nombreUsuario, ' ha eliminado la memoria: Titulo = ', v_tituloMemoria)
        );

        COMMIT;

        SELECT 'Memoria dada de baja exitosamente' AS mensaje;
    ELSE
        ROLLBACK;
        SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = 'No tenés permisos para dar de baja esta memoria';
    END IF;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_EliminarCuenta` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_EliminarCuenta`(
    IN p_idUsuario INT,
    IN p_contrasena VARCHAR(45)
)
BEGIN
    DECLARE v_contrasenaDB VARCHAR(45);
    DECLARE v_nombreUsuario VARCHAR(45);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'Error al eliminar la cuenta' AS mensaje;
    END;

    START TRANSACTION;

    SELECT contrasena, nombre INTO v_contrasenaDB, v_nombreUsuario
    FROM usuario
    WHERE idUsuario = p_idUsuario
    FOR UPDATE;

    IF v_contrasenaDB IS NULL THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Usuario no encontrado';

    ELSEIF v_contrasenaDB != p_contrasena THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = 'Contraseña incorrecta';

    ELSE
        CALL sp_RegistrarLog(
            p_idUsuario,
            'Eliminar Cuenta',
            CONCAT(v_nombreUsuario, ' ha eliminado su cuenta')
        );

        DELETE FROM memoria WHERE idUsuario = p_idUsuario;
        DELETE FROM log WHERE idUsuario = p_idUsuario;
        DELETE FROM usuario WHERE idUsuario = p_idUsuario;

        COMMIT;

        SELECT 'Cuenta eliminada correctamente' AS mensaje;
    END IF;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_IniciarSesion` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_IniciarSesion`(
    IN p_mail VARCHAR(45),
    IN p_contrasena VARCHAR(45)
)
BEGIN
    DECLARE v_count INT;
    DECLARE v_idUsuario INT;
    DECLARE v_nombre VARCHAR(45);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SELECT 'Error al iniciar sesión' AS mensaje;
    END;

    SELECT COUNT(*), MAX(idUsuario), MAX(nombre)
    INTO v_count, v_idUsuario, v_nombre
    FROM usuario
    WHERE mail = p_mail AND contrasena = p_contrasena;

    IF v_count = 1 THEN
        START TRANSACTION;

        CALL sp_RegistrarLog(
            v_idUsuario,
            'Iniciar Sesión',
            CONCAT(v_nombre, ' ha iniciado sesión')
        );

        SELECT idUsuario, nombre, mail, idRol,
               'Inicio de sesión exitoso' AS mensaje
        FROM usuario
        WHERE mail = p_mail AND contrasena = p_contrasena;

        COMMIT;
    ELSE
        SIGNAL SQLSTATE '45002' SET MESSAGE_TEXT = 'Correo o contraseña incorrectos';
    END IF;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_RegistrarLog` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_RegistrarLog`(
    IN p_idUsuario INT,
    IN p_descripcionProcedimiento VARCHAR(45),
    IN p_detalle VARCHAR(500)
)
BEGIN
    DECLARE v_idProcedimiento INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN END;

    SELECT idProcedimiento INTO v_idProcedimiento
    FROM procedimiento
    WHERE descripcion = p_descripcionProcedimiento
    LIMIT 1;

    IF v_idProcedimiento IS NOT NULL THEN
        INSERT INTO log (fecha_hr, idUsuario, idProcedure, detalle)
        VALUES (NOW(), p_idUsuario, v_idProcedimiento, p_detalle);
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-26 19:22:01
