<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" 	uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
<head>
<title><s:message code="video.torrent"/></title>
<style type="text/css">
.text {
	font-size: 11px; border:0; background-color: lightgray;
}
.text:focus {
	background-color:yellow;
}
.studio {
	width: 60px;
}
.opus {
	width: 60px; 
}
.title {
	width: 300px;
}
.mark {
	background-color:orange;
	background: url('<c:url value="/res/img/yes_check.png"/>');
	background-size: 30px 25px;
	background-repeat: no-repeat;
}
</style>
<script type="text/javascript">
$(document).ready(function(){
	$("td").addClass("nowrap");
});

/**
 * 비디오 확인을 기억하기 위해 css class를 변경한다.
 */
function fnMarkChoice(opus) {
	$("#check-" + opus).addClass("mark");
	//$("#check-" + opus).hide();
	//$("#check-" + opus + " > *").attr("disabled",true);
}
function searchInput(keyword) {
	$("div#content_div input").each(function() {
		if ($(this).val().toLowerCase().indexOf(keyword.toLowerCase()) > -1) {
			$(this).parent().parent().show();
		}
		else {
			$(this).parent().parent().hide();
		}
	});
}
function viewCover(opus) {
	popupImage(context + "video/" + opus + "/cover", "torrent");
}
function fnGoSearch(opus) {
	fnMarkChoice(opus);
	popup('<c:url value="/video/torrent/search/"/>' + opus, 'torrentSearch', 900, 950);
}
</script>
</head>
<body>

<div id="header_div" class="div-box">
	<s:message code="video.total"/> <s:message code="video.video"/> : ${fn:length(videoList)}
	<input type="search" name="search" id="search" style="width:200px;" 
		class="searchInput" placeHolder="<s:message code="video.search"/>" 
		onkeyup="searchInput(this.value)"/>
	<!-- <a href="http://www.akiba-online.com" target="_blank">www.akiba-online.com</a> -->
	<span class="button" onclick='$(".newWin").toggle(); $(".popup").toggle();' style="float:right;">Popup mode</span>
</div>

<div id="content_div" class="div-box" style="overflow:auto;">
	<table class="video-table" style="background-color:lightgray">
		<c:forEach items="${videoList}" var="video" varStatus="status">
		<tr id="check-${video.opus}">
			<td align="right">
				${status.count}
			</td>
			<td>
				<span class="label newWin" style="display:none;">
					<a onclick="fnMarkChoice('${video.opus}'); viewCover('${video.opus}');" 
						href="<s:eval expression="@prop['video.torrent.url']"/>${video.opus}" 
						target="_blank" class="link">Torrent newWin</a></span>
				<span class="label popup"><a onclick="fnGoSearch('${video.opus}');" class="link">Torrent popup</a></span>
			</td>
			<td>
				<input value="${video.studio}" class="text studio" onclick="fnViewStudioDetail('${video.studio}')" />
			</td>
			<td>
				<input value="${video.opus}" class="text opus" />
			</td>
			<td>
				<input value="${video.title}" class="text title" onclick="fnViewVideoDetail('${video.opus}')" />
			</td>
			<td style="width:100%;">
				<c:forEach items="${video.videoCandidates}" var="candidate">
				<form method="post" target="ifrm" action="<c:url value="/video/${video.opus}/confirmCandidate"/>">
					<input type="submit" value="${candidate.name}" onclick="fnMarkChoice('${video.opus}')"/>
					<input type="hidden" name="path" value="${candidate.absolutePath}"/>
				</form>
				</c:forEach>
			</td>
		</tr>
		</c:forEach>
	</table>
</div>

</body>
</html>
