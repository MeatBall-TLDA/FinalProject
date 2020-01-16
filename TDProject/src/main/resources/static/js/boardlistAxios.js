// 전체 게시글 수 받을 변수
var count

// 게시글 종류에 따라 url값 받아줄 변수 
var countUrl = "getCount"

if (sessionStorage.getItem("boardKind") == 0 && sessionStorage.getItem("categoryKind") != null) {
   countUrl = "getCategoryCount"
} else if (sessionStorage.getItem("boardKind") == 0 && sessionStorage.getItem("hashtagKind") != null) {
   countUrl = "getHashtagCount"
} 

// 전체 게시글 리스트
var boardArray

// vue객체 받아줄 변수 리스트
var vueInstance = []

// sorting 종류 받을 변수
var sortKind

function makeUrl() {
   if (sessionStorage.getItem("boardKind") == 0 && sessionStorage.getItem("categoryKind") != null) {
      countUrl = "getCategoryCount"
   } else if (sessionStorage.getItem("boardKind") == 0 && sessionStorage.getItem("hashtagKind") != null) {
      countUrl = "getHashtagCount"
   } 
}



// 전체 게시글 수 얻어오기
function getCount() {
   var url = "getCount"

   if (countUrl == "getCategoryCount") {
      url = countUrl + "?category=" + sessionStorage.getItem("categoryKind")
   } else if (countUrl == "getHashtagCount") {
      url = countUrl + "?hashtag=" + sessionStorage.getItem("hashtagKind")
   }

   axios.get("http://localhost:8000/" + url)
      .then(resData => {
         count = resData.data
      }).catch(error => {
         console.log(error)
      })
}

function getBoard(pageNum, sorting) {
   scKind = "desc"
   if (sorting == undefined || sorting == "게시날짜") {
      sortKind = "postingDate"
      scKind = "asc"
   } else if (sorting == "개봉임박") {
      sortKind = "openDate"
      scKind = "asc"
   } else if (sorting == "좋아요") {
      sortKind = "heart"
   }

   var url = "http://localhost:8000/getHidden?page=" + pageNum + "&sort=" + sortKind + "," + scKind

   if (sessionStorage.getItem("boardKind") == 0 && sessionStorage.getItem("categoryKind") != null) {
      url = "http://localhost:8000/categorySearch?page=" + pageNum + "&sort=" + sortKind + "," + scKind + "&category=" + sessionStorage.getItem("categoryKind")
   } else if (sessionStorage.getItem("boardKind") == 0 && sessionStorage.getItem("hashtagKind") != null) {
      url = "http://localhost:8000/hashtagSearch?page=" + pageNum + "&sort=" + sortKind + "," + scKind + "&hashtag=" + sessionStorage.getItem("hashtagKind").replace("#", "")
   } 

   axios.get(url)
      .then(resData => {
         getCount()
         boardArray = resData.data.content
         console.log(boardArray)
         setBoard(count, resData.data.content, pageNum)
      }).catch(error => {
         console.log(error)
      })

   document.getElementById("row1").scrollIntoView();
}

function setBoard(boardCount, boardArray, pageNum) {
   var forNum = boardCount - pageNum * 10 < 10 ? boardCount - pageNum * 10 : 10
   for (let i = 0; i < forNum; i++) {
      board = boardArray[i]
      vueInstance[i].plusClaim = `<span class="mx-2" style="cursor:pointer;">신고하기</span><i class="fas fa-user-slash"></i>`
      vueInstance[i].ifflag = false
      vueInstance[i].heartflag = false
      document.getElementById("parent" + (i + 1)).style = document.getElementById("parent" + (i + 1)).style == "display:none" ? "display:unset" :
         document.getElementById("child" + (i + 1)).innerHTML =
         `<div class="text pt-1 mt-1">
               <span class="category mb-1 d-block"><a href="#">` + board.category + `</a></span>
               <h1 class="cho-font"><a style="color:black">` + board.title + `</a></h1>
               <span style=padding-left:10rem>` + board.nickname + `</span>
               <p class="h4 mt-5 mb-4 cho-font" style="color:darkgray">` + board.contents + `</p>
               <div class="h6 cho-font">`+ board.hashtag + `</div>
               <div class="mb-2 d-flex align-items-center">
               <div class="m-auto info">
               <h4 class="cho-font">게시일 <span class="h5 text-secondary cho-font">` + updateDate(board.postingDate) + `</span></h4>
               <h4 class="cho-font">작품공개일 <span class="h5 text-secondary cho-font">` + updateDate(board.openDate) + `</span></h4>
               </div></div></div>`

      if (board.plusHeartUserId != null) {
         board.plusHeartUserId.includes(user) ? vueInstance[i].plusHeart = "<i class=\"heart fas fa-heart\" onclick=\"heart(this)\" style=\"color:red\"></i>" : vueInstance[i].plusHeart = "<i class=\"heart far fa-heart\" onclick=\"heart(this)\" style=\"color:red\"></i>"
      }
      vueInstance[i].boardHeartNum = board.heart
      if(user != "null"){
         board.nickname == user ? vueInstance[i].deletebutton =  "<br><div class='btn btn-outline-primary mt-1'>삭제하기</div>": vueInstance[i].deletebutton = ""
         }else{
            vueInstance[i].deletebutton = ""
         }
   }
   for (let j = forNum; j < 10; j++) {
      document.getElementById("parent" + (j + 1)).style = "display:none"
   }
}

function heart(heart) {
   heart.classList.toggle('far');
   heart.classList.toggle('fas');
}

function updateDate(date) {
   var update = "";
   var year = date.substr(0, 4).concat("년")
   var month = date.substr(4, 2).concat("월")
   var day = date.substr(6, 2).concat("일")
   var update = year + " " + month + " " + day
   return update
}

for (let i = 1; i <= 10; i++) {
   vueInstance[i - 1] = new Vue({
      el: "#row" + i,
      data: {
         tag: "",
         ifflag: false,
         heartflag: false,
         boardHeartNum: 0,
         repHeartNum: 0,
         deletebutton : "<br><div class='btn btn-outline-primary mt-1'>삭제하기</div>",
         plusHeart: `<i class="heart far fa-heart" onclick="heart(this)" style="color:red"></i>`,
         repPlusHeart: `<i class="heart far fa-heart" onclick="heart(this)" style="color:red"></i>`,
         plusClaim: `<span class="mx-2" style="cursor:pointer;">신고하기</span><i class="fas fa-user-slash"></i>`
      },
      methods: {
         comment: function () {

            axios.get("http://localhost:8000/getReply?repUserId=" + user + "&repBoardId=" + boardArray[i - 1].id)
               .then(resData => {
                  if (resData.data.length == 0) {
                     this.ifflag = true
                     this.heartflag = false
                     this.tag = "<br><div class='padding10-kim'><textarea id=\"input" + i + "\"class=\"form-control rounded\""
                        + "placeholder=\"5자 이상 입력해주세요\" style=\"resize: none;\"></textarea><br>"
                        + "<button type=\"button\" class=\"btn btn-outline-secondary py-3 px-5\" onclick=\"makeReply(" + i + ")\">댓글 남기기</button></div>"

                  } else {
                     this.repHeartNum = resData.data.repHeart
                     this.ifflag = !this.ifflag
                     this.heartflag = !this.heartflag
                     this.tag = `<hr><span class="text pt-5 mt-3">
                              <span class="align-items-center">
                              <span class="col-6 p-0">
                              <h4 class="cho-font text-secondary">` + resData.data.repContents + `<h4>
                              <h3 class="cho-font">` + resData.data.userId + ` <span class="h5 text-secondary cho-font">` + updateDate(resData.data.repPostingDate) + `</span></h3>
                              </span></span></span>`
                     if (resData.data.plusHeartUserId != null) {
                        resData.data.plusHeartUserId.includes(user) ? this.repPlusHeart = `<i class="heart fas fa-heart" onclick="heart(this)" style="color:red"></i>` : this.repPlusHeart = `<i class="heart far fa-heart" onclick="heart(this)" style="color:red"></i>`
                     }
                  }
               }).catch(error => {
                  console.log(error)
               })
         },
         repHeart: function () {
            axios.post("http://localhost:8000/plusHeart?repUserId=" + user + "&repBoardId=" + boardArray[i - 1].id)
               .then(resData => {
                  this.repHeartNum = resData.data
               }).catch(error => {
                  console.log(error)
               })
         },
         boardHeart: function () {
            axios.post("http://localhost:8000/plusBoardHeart?nickname=" + user + "&id=" + boardArray[i - 1].id)
               .then(resData => {
                  this.boardHeartNum = resData.data
               }).catch(error => {
                  console.log(error)
               })
         },
         makeClaim: function () {
            axios.post("http://localhost:8000/plusBoardClaim?nickname=" + user + "&id=" + boardArray[i - 1].id)
               .then(resData => {
                  alert(resData.data)
               }).catch(error => {
                  console.log(error)
               })
         },
         deleteBoard: function(){
            axios.post("http://localhost:8000/deleteHidden?boardId=" + boardArray[i - 1].id)
            .then(resData => {
               document.getElementById("row" + i).innerHTML = "<h2 class='cho-font'>삭제되었습니다</h2>" 
            }).catch(error =>{
               console.log(error)
            })
         }
      }
   })
}

// innerHTML코드 반복문 함수화
function setPageNumber(direction, pageNum, loopNum) {
   paging = ""
   for (var i = 1; i <= loopNum; i++) {
      if (i == 1 && direction == 1) {
         paging += " <li class='page-item active'><a class='page-link' id='pageNum" + i + "' onclick='getBoard(" + (pageNum + i - 1) + ",\"sortKind\")' href='javascript:void(0)'>" + (pageNum + i) + "</a></li>"
      } else if (i == loopNum && direction == 2) {
         paging += " <li class='page-item active'><a class='page-link' id='pageNum" + i + "' onclick='getBoard(" + (pageNum + i - 1) + ",\"sortKind\")' href='javascript:void(0)'>" + (pageNum + i) + "</a></li>"
      } else {
         paging += " <li class='page-item'><a class='page-link' id='pageNum" + i + "' onclick='getBoard(" + (pageNum + i - 1) + ",\"sortKind\")' href='javascript:void(0)'>" + (pageNum + i) + "</a></li>"
      }
   }
   return paging
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
   var endPoint = count % 10 == 0 ? parseInt(count / 10) : parseInt(count / 10) + 1
   parseInt(count / 10) + 1

   // 실제로 들어갈 페이지값
   var nextPage = (standard + 1) * 5
   var prevPage = (standard - 1) * 5

   // 반복되는 구문 초기화
   const next = " <li class='page-item'><a class='page-link' onclick='setPaging(1)' href='javascript:void(0)'>다음</a></li>"
   const prev = "<li class='page-item'><a class='page-link' onclick='setPaging(2)' href='javascript:void(0)'>이전</a></li>"

   // 다음버튼 클릭시
   if (direction == 1) {
      getBoard(nextPage)
      paging = prev
      paging += Number(nowNum) + 5 >= endPoint ? setPageNumber(direction, nextPage, endPoint - (standard + 1) * 5) : setPageNumber(direction, nextPage, 5) + next
      // 이전버튼 클릭시
   } else {
      getBoard(prevPage + 4)
      paging += standard == 1 ? setPageNumber(direction, prevPage, 5) : prev + setPageNumber(direction, prevPage, 5)
      paging += next
   }
   document.getElementById("paging").innerHTML = paging
}

function getBoardAndSetPage(pageNum, sorting) {
   getBoard(pageNum, sorting)
   if (count < 50) {
      document.getElementById("paging").innerHTML = setPageNumber(1, 0, parseInt((count - 1) / 10) + 1)
   } else {
      document.getElementById("paging").innerHTML = `<li class="page-item active"><a class="page-link" id="pageNum1" onclick="getBoard(0)" href="javascript:void(0)">1</a></li>
   <li class="page-item"><a class="page-link" id="pageNum2" onclick="getBoard(1)" href="javascript:void(0)">2</a></li>
   <li class="page-item"><a class="page-link" id="pageNum3" onclick="getBoard(2)" href="javascript:void(0)">3</a></li>
   <li class="page-item"><a class="page-link" id="pageNum4" onclick="getBoard(3)" href="javascript:void(0)">4</a></li>
   <li class="page-item"><a class="page-link" id="pageNum5" onclick="getBoard(4)" href="javascript:void(0)">5</a></li>
   <li class="page-item"><a class="page-link" onclick="setPaging(1)" href="javascript:void(0)">다음</a></li>`
   }

   document.getElementById("row1").scrollIntoView();
}

// 페이징 처리 이벤트 추가
$(document).on("click", "#topheader .pagination a", function () {
   $("#topheader .pagination").find("li.active").removeClass("active");
   $(this).parent("li").addClass("active");
});

function makeReply(replyNum) {
   var today = new Date()
   var dd = today.getDate()
   var mm = today.getMonth() + 1
   var yyyy = today.getFullYear()

   dd = dd < 10 ? "0" + dd : dd
   mm = mm < 10 ? "0" + mm : mm

   var mass = document.getElementById("input" + replyNum).value.split("\n")
   var repContent = ""
   mass.forEach(element => {
      repContent += element + "<br>"
   });

   axios.post("http://localhost:8000/saveReply?userId=" + user + "&repBoardId=" + boardArray[replyNum - 1].id + "&repContents=" + repContent
      + "&repPostingDate=" + yyyy + mm + dd + "&repHeart=0&repClaim=0")
      .then(resData => {
         if (resData.data == 1) {
            vueInstance[replyNum - 1].tag = `<hr><span class="text pt-2 mt-3">
                     <span class="align-items-center">
                     <span class="col-6 p-0">
                     <h4 class="cho-font text-secondary">` + repContent + `<h4>
                     <h3 class="cho-font">` + user + ` <span class="h5 text-secondary cho-font">` + yyyy + "년 " + mm + "월 " + dd + `일</span></h3>
                     </span></span></span>`

            vueInstance[replyNum - 1].heartflag = !vueInstance[replyNum - 1].heartflag
            vueInstance[replyNum - 1].repHeartNum = 0
         } else {
            alert("저장에 실패했습니다")
         }
      }).catch(error => {
         console.log(error)
      })
}

// 페이지 로딩시 바로 실행되는 로직
function getAxios() {
   var url = "getCount"

   if (countUrl == "getCategoryCount") {
      url = countUrl + "?category=" + sessionStorage.getItem("categoryKind")
   } else if (countUrl == "getHashtagCount") {
      url = countUrl + "?hashtag=" + sessionStorage.getItem("hashtagKind").replace("#", "")
   }

   axios.get("http://localhost:8000/" + url)
      .then(resData => {
         count = resData.data
         // 첫번째 페이지 게시글 가져오고 화면에 뿌려주기
         getBoard(0)
         if (resData.data <= 50) {
            // 전체 게시글수가 50 이하일경우 다음 페이지가 나오면 안되기 때문에 조건식 생성
            document.getElementById("paging").innerHTML = setPageNumber(1, 0, parseInt((resData.data - 1) / 10) + 1)
         }
      }).catch(error => {
         console.log(error)
      })
}

getAxios();

window.onload = function () {
   history.replaceState(null, null, "/close");
   if (sessionStorage.getItem("boardKind") != 0) {
      sessionStorage.removeItem("categoryKind")
      sessionStorage.removeItem("hashtagKind")
      sessionStorage.removeItem("boardKind")
   }
}


