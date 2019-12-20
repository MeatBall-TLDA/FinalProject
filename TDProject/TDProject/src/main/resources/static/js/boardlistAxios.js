  // 전체 게시글 수 받을 변수
      var count

      // 전체 게시글 리스트
      var boardArray

      // 전체 게시글 수 얻어오기
      function getCount() {
         axios.get("http://127.0.0.1:8000/getCount")
            .then(resData => {
               count = resData.data
            }).catch(error => {
               console.log(error)
            })
      }

      function getBoard(pageNum) {
         axios.get("http://127.0.0.1:8000/getHidden?page=" + pageNum + "&sort=hashtag,desc")
            .then(resData => {
               getCount()
               boardArray = resData.data.content
               setBoard(count, resData.data.content, pageNum)
            }).catch(error => {
               console.log(error)
            })
      }

      function setBoard(boardCount, boardArray, pageNum) {
         var forNum = boardCount - pageNum * 10 < 10 ? boardCount - pageNum * 10 : 10
         for (let i = 0; i < forNum; i++) {
            board = boardArray[i]
            document.getElementById("row" + (i + 1)).innerHTML = "<div class=\"text pt-2 mt-3\">"
               /*"<span>"
               + "id : " + boardArray[i].id + "<br>"
               + "contents : " + boardArray[i].contents + "<br>"
               + "hashtag : " + boardArray[i].hashtag + "<br>"
               + "postingDate : " + boardArray[i].postingDate + "<br>"
               + "openDate : " + boardArray[i].openDate + "<br>"
               + "heart : " + boardArray[i].heart + "<br>"
               + "claim : " + boardArray[i].claim + "<br>"
               + "nickname : " + boardArray[i].nickname + "<br>"
               + "category : " + boardArray[i].category + "<br>"
               + "</span>"
               + "<br><button onclick='comment(" + i + ")'>댓글 남기기</button><br>"
               + "<span id=c" + i + "></span>" */
					+"<span class=\"category mb-1 d-block\"><a href=\"#\">" + board.category + "</a></span>"
					+"<h3 class=\"mb-4\">싸늘한바람<a href=\"#\"></a></h3>"
					+"<p class=\"mb-4\">"+ board.contents+"</p>"
				   +"<div class=\"author mb-4 d-flex align-items-center\">"
				   +"<div class=\"ch-3 info\">"
				   +"<span>작성자</span>"
				   +"<h3><a href=\"#\">"+ board.nickname + "</a>,<span>"+ board.postingDate +"</span></h3>"
               +"<h3><span>감성개봉일,"+ board.openDate +"</span></h3>"
				   +"</div></div>"
				   +"<div class=\"meta-wrap d-md-flex align-items-center\">"
				   +"<div class=\"half order-md-last text-md-right\">"
					+"<p class=\"meta\">"
					+"<span><i class=\"icon-heart\"></i>"+ board.heart + "</span>"
               +"<span><i class=\"icon-comment\"></i>"+ "300" + "</span>"
               +"<span><i class=\"icon-error\"></i>"+ "300" + "</span>"
               +"</p></div><div class=\"half\"></div></div></div>"
               +"<div><div class=\"form-group\">"
               +"<textarea name id cols=\"30\" rows=\"7\" class=\"form-control-cho\" placeholder=\"Message\"></textarea>"
               +"</div><div class=\"form-group\">"
               +"<input type=\"submit\" value=\"댓글 남기기\" class=\"btn btn-primary py-3 px-5\">"
               +"</div></div>"
         }
         for (let j = forNum; j < 10; j++) {
            document.getElementById("row" + (j + 1)).innerHTML = "<span></span>"
         }
      }

      // 페이징 처리 로직
      function setPaging(direction) {
         // 실시간 전체 게시글 수 동기화
         getCount()

         // innerHTML 받아줄 변수 선언
         var paging = ""

         // 페이징 처리를 위해 기준값 계산
         var standard = parseInt(document.getElementById('pageNum1').innerText / 5)

         var nowNum = Number(document.getElementById('pageNum1').innerText) + 4

         // 끝나는 페이지값 계산
         var endPoint = parseInt(count / 10) + 1
         parseInt(count / 10) + 1

         // 실제로 들어갈 페이지값
         var nextPage = (standard + 1) * 5
         var prevPage = (standard - 1) * 5

         // 반복되는 구문 초기화
         const next = " <a id='nextPage' onclick='setPaging(1)' href='javascript:void(0)'>다음</a>"
         const prev = "<a id='prevPage' onclick='setPaging(2)' href='javascript:void(0)'>이전</a>"

         // 다음버튼 클릭시
         if (direction == 1) {
            getBoard(nextPage)
            paging = prev
            paging += Number(nowNum) + 5 > endPoint ? setPageNumber(nextPage, endPoint - (standard + 1) * 5) : setPageNumber(nextPage, 5) + next
            // 이전버튼 클릭시   
         } else {
            getBoard(prevPage + 4)
            paging += standard == 1 ? setPageNumber(prevPage, 5) : prev + setPageNumber(prevPage, 5)
            paging += next
         }
         document.getElementById("paging").innerHTML = paging
      }

      // innerHTML코드 반복문 함수화
      function setPageNumber(pageNum, loopNum) {
         paging = ""
         for (var i = 1; i <= loopNum; i++) {
            paging += " <a id='pageNum" + i + "' onclick='getBoard(" + (pageNum + i - 1) + ")' href='javascript:void(0)'>" + (pageNum + i) + "</a>"
         }
         return paging
      }

      // 페이지 로딩시 바로 실행되는 로직
      function getAxios(){
      axios.get("http://127.0.0.1:8000/getCount")
         .then(resData => {
            // 첫번째 페이지 게시글 가져오고 화면에 뿌려주기
            getBoard(0)
            if (resData.data <= 50) {
               // 전체 게시글수가 50 이하일경우 다음 페이지가 나오면 안되기 때문에 조건식 생성
               document.getElementById("paging").innerHTML = setPageNumber(0, parseInt((resData.data - 1) / 10) + 1)
            }
         }).catch(error => {
            console.log(error)
         })
    }
getAxios();