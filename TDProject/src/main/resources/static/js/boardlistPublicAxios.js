

      /* 하트 생기고 안생기고 나오는것 */
      function heart(heart) {
        heart.classList.toggle('far');
        heart.classList.toggle('fas');
     }

     /* 년 월 일 수정해서 반환 */
     function updateDate(date) {
        var update = "";
        var year = date.substr(0, 4).concat("년")
        var month = date.substr(4, 2).concat("월")
        var day = date.substr(6, 2).concat("일")
        var update = year + " " + month + " " + day
        return update
     }


     // 전체 게시글 수 받을 변수
     var count

     // 전체 게시글 리스트
     var boardArray

     // vue객체 받아줄 변수 리스트
     var vueInstance = []

     // sorting 종류 받을 변수
     var sortKind

     // 댓글 data
     var repData = []

     // 전체 게시글 수 얻어오기
     function getCount() {
        axios.get("http://127.0.0.1:8000/getCount")
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
        } else if (sorting == "개봉임박") {
           sortKind = "openDate"
           scKind = "asc"
        } else if (sorting == "좋아요") {
           sortKind = "heart"
        }

        axios.get("http://127.0.0.1:8000/getHidden?page=" + pageNum + "&sort=" + sortKind + "," + scKind)
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
           vueInstance[i].ifflag = false
           vueInstance[i].heartflag = false
           repData[i].ifflag = false
           document.getElementById("child" + (i + 1)).innerHTML =
              '<div class="text pt-1 mt-1">'
              + '<span class="category mb-1 d-block"><a class="cho-font-size" href="#">' + board.category + '</a></span>'
              + '<h1 class="mb-4 cho-font"><a style="color:black">Title 싸늘한 바람</a></h1>'
              + '<p class="h3 mb-4 cho-font" style="color:darkgray">' + board.contents + '</p>'
              + '<div class="h5 cho-font">' + board.hashtag + '</div>'
              + '<div class="mb-2 d-flex align-items-center">'
              + '<div class="ch-3 info">'
              + '<span class="h6">작성자</span>'
              + '<h4 class="cho-font" style="font-size: 30px;">' + board.nickname + ', <span class="h4 text-secondary cho-font">' + updateDate(board.postingDate) + '</span></h4>'
              + '<h4 class="cho-font" style="font-size: 30px;">감성개봉일, <span class="h4 text-secondary cho-font">' + updateDate(board.openDate) + '</span></h4>'
              + '</div></div></div>'
           /* "id : " + boardArray[i].id + "<br>"
            + "contents : " + boardArray[i].contents + "<br>"
            + "hashtag : " + boardArray[i].hashtag + "<br>"
            + "postingDate : " + boardArray[i].postingDate + "<br>"
            + "openDate : " + boardArray[i].openDate + "<br>"
            + "claim : " + boardArray[i].claim + "<br>"
            + "nickname : " + boardArray[i].nickname + "<br>"
            + "category : " + boardArray[i].category + "<br>"
            + "좋아요 : "*/
           if (board.plusHeartUserId != null) {
              board.plusHeartUserId.includes("유저닉네임") ? vueInstance[i].plusHeart = '<i class="heart fas fa-heart" onclick="heart(this)" style="color:red"></i>' : vueInstance[i].plusHeart = '<i class="heart far fa-heart" onclick="heart(this)" style="color:red"></i>'
           }
           vueInstance[i].boardHeartNum = board.heart
           vueInstance[i].plusClaim = '<span class="mx-1" style="cursor:pointer;">신고하기</span><i class="fas fa-user-slash"></i>'
        }
        for (let j = forNum; j < 10; j++) {
           vueInstance[j].ifflag = false
           vueInstance[j].heartflag = false
           vueInstance[j].boardHeartNum = ""
           vueInstance[j].plusHeart = ""
           vueInstance[j].plusClaim = ""
           repData[j].ifflag = false
           document.getElementById("child" + (j + 1)).innerHTML = "<span></span>"
        }
     }


     for (let i = 1; i <= 10; i++) {
        repData[i - 1] = {
           tag: [],
           repUserId: [],
           ifflag: false,
           repHeartNum: [],
           plusHeart: [],
           plusClaim: '<span class="mx-1" style="cursor:pointer;">신고하기</span><i class="fas fa-user-slash"></i>'
        }

        vueInstance[i - 1] = new Vue({
           el: "#row" + i,
           data: {
              tag: "",
              ifflag: false,
              heartflag: false,
              boardHeartNum: 0,
              repHeartNum: 0,
              plusHeart: "",
              plusClaim: '<span class="mx-1" style="cursor:pointer;">신고하기</span><i class="fas fa-user-slash"></i>'
           },
           computed: {
              dynamicComponent: function () {
                 return {
                    data: function () {
                       return repData[i - 1]
                    },
                    template: `<div>${this.tag}</div>`,
                    methods: {
                       repHeart: function (num) {
                          axios.post("http://127.0.0.1:8000/plusHeart?repUserId=" + repData[i - 1].repUserId[num] + "&repBoardId=" + boardArray[i - 1].id + "&nickName=" + "유저닉네임")
                             .then(resData => {
                                vueInstance[i - 1].dynamicComponent.data().repHeartNum.splice(num, 1, resData.data)
                             }).catch(error => {
                                console.log(error)
                             })
                       },
                       moreReply: function (loopNum) {
                          axios.get("http://127.0.0.1:8000/getReplyInOpen?page=" + loopNum + "&repBoardId=" + boardArray[i - 1].id)
                             .then(resData => {

                                var repTag = ""
                                var standardLength = 5 * (loopNum + 1)
                                var length = resData.data.totalElements > standardLength ? standardLength : resData.data.content.length + 5 * loopNum

                                for (let j = 5 * loopNum; j < length; j++) {
                                   repData[i - 1].repHeartNum[j] = resData.data.content[j - 5 * loopNum].repHeart
                                   repData[i - 1].tag[j] = '<span class="text pt-2 mt-3">'
                                      + '<span class="align-items-center">'
                                      + '<span class="col-6 p-0">'
                                      + '<h2 class="cho-font" style="margin-bottom: 0px;">' + resData.data.content[j - 5 * loopNum].userId + ', <span class="h5 text-secondary cho-font">' + updateDate(resData.data.content[j - 5 * loopNum].repPostingDate) + '</span></h2>'
                                      + '<h4 class="cho-font text-secondary">' + resData.data.content[j - 5 * loopNum].repContents + '<h4>'
                                      + '</span></span></span>'
                                   if (resData.data.content[j - 5 * loopNum].plusHeartUserId != null) {
                                      resData.data.content[j - 5 * loopNum].plusHeartUserId.includes("유저닉네임") ? repData[i - 1].plusHeart[j] = '<i class="heart fas fa-heart" onclick="heart(this)" style="color:red"></i>' : repData[i - 1].plusHeart[j] = '<i class="heart far fa-heart" onclick="heart(this)" style="color:red"></i>'
                                   } else {
                                      repData[i - 1].plusHeart[j] = '<i class="heart far fa-heart" onclick="heart(this)" style="color:red"></i>'
                                   }
                                   /* "<br>유저아이디 : " + resData.data.content[j - 5 * loopNum].userId + "<br>"
                                      + "댓글 내용 : " + resData.data.content[j - 5 * loopNum].repContents + "<br>"
                                      + "게시 날짜 : " + resData.data.content[j - 5 * loopNum].repPostingDate + "<br>"
                                      + "좋아요 : " */
                                   repData[i - 1].repUserId[j] = resData.data.content[j - 5 * loopNum].userId

                                   repTag +=
                                      `
                                      <hr>
                                      <div class="pl-5 ml-md-3 pl-md-3 ml-lg-5 pl-lg-5">
                                      <span v-html="tag[` + j + `]" v-if="ifflag"></span>
                                      <span v-text="repHeartNum[` + j + `]" v-if="ifflag"></span>
                                      <span v-html="plusHeart[` + j + `]" v-on:click="repHeart(` + j + `)" v-if="ifflag"></span>
                                      <span v-html="plusClaim" v-on:click="repMakeClaim(` + j + `)" v-if="ifflag"></span><br><br>
                                      </div>
                                      `
                                }
                                var removeLast = ""
                                var removeLastLength = vueInstance[i - 1].tag.split("\n").length - 1
                                vueInstance[i - 1].tag.split("\n").forEach(function (element, index, array) {
                                   removeLast += index == removeLastLength ? "" : element + "\n"
                                });

                                vueInstance[i - 1].tag = resData.data.totalElements > standardLength
                                   ? removeLast + repTag + `<div class="pl-5 ml-md-3 pl-md-3 ml-lg-5 pl-lg-5"><a href="javascript:void(0)" v-on:click="moreReply(` + (loopNum + 1) + `)">댓글 더보기</a></div>`
                                   : removeLast + repTag + `<div class="pl-5 ml-md-3 pl-md-3 ml-lg-5 pl-lg-5"><a href="javascript:void(0)" v-on:click="disappear">댓글 접기</a></div>`
                             }).catch(error => {
                                console.log(error)
                             })
                       },
                       repMakeClaim: function (num) {
                          axios.post("http://127.0.0.1:8000/plusRepClaim?repUserId=" + repData[i - 1].repUserId[num] + "&repBoardId=" + boardArray[i - 1].id)
                             .then(resData => {
                                alert(resData.data)
                             }).catch(error => {
                                console.log(error)
                             })
                       },
                       disappear: function () {
                          repData[i - 1].ifflag = !repData[i - 1].ifflag
                          vueInstance[i - 1].tag = ""
                       }
                    }
                 }
              }
           },
           methods: {
              comment: function () {
                 if (repData[i - 1].ifflag == false) {
                    axios.get("http://127.0.0.1:8000/getReplyInOpen?repBoardId=" + boardArray[i - 1].id)
                       .then(resData => {

                          var repTag = ""
                          var length = resData.data.content.length
                          repData[i - 1].ifflag = !repData[i - 1].ifflag

                          for (let j = 0; j < length; j++) {
                             repData[i - 1].repHeartNum[j] = resData.data.content[j].repHeart
                             repData[i - 1].tag[j] = '<span class="text mt-3">'
                                + '<span class="align-items-center">'
                                + '<span class="col-6 p-0">'
                                + '<h2 class="cho-font" style="margin-bottom: 0px;">' + resData.data.content[j].userId + ', <span class="h5 text-secondary cho-font">' + updateDate(resData.data.content[j].repPostingDate) + '</span></h2>'
                                + '<h4 class="cho-font text-secondary">' + resData.data.content[j].repContents + '<h4>'
                                + '</span></span></span>'
                             if (resData.data.content[j].plusHeartUserId != null) {
                                resData.data.content[j].plusHeartUserId.includes("유저닉네임") ? repData[i - 1].plusHeart[j] = '<i class="heart fas fa-heart" onclick="heart(this)" style="color:red"></i>' : repData[i - 1].plusHeart[j] = '<i class="heart far fa-heart" onclick="heart(this)" style="color:red"></i>'
                             } else {
                                repData[i - 1].plusHeart[j] = '<i class="heart far fa-heart" onclick="heart(this)" style="color:red"></i>'
                             }
                             /*"<br>유저아이디 : " + resData.data.content[j].userId + "<br>"
                                + "댓글 내용 : " + resData.data.content[j].repContents + "<br>"
                                + "게시 날짜 : " + resData.data.content[j].repPostingDate + "<br>"
                                + "좋아요 : " */
                             repData[i - 1].repUserId[j] = resData.data.content[j].userId

                             repTag +=
                                `
                                <hr>
                                <div class="pl-5 ml-md-3 pl-md-3 ml-lg-5 pl-lg-5">
                                <span v-html="tag[` + j + `]" v-if="ifflag"></span>
                                <span>
                                <span v-text="repHeartNum[` + j + `]" v-if="ifflag"></span>
                                <span v-html="plusHeart[` + j + `]" v-on:click="repHeart(` + j + `)" v-if="ifflag"></span>
                                <span v-html="plusClaim" v-on:click="repMakeClaim(` + j + `)" v-if="ifflag"></span><br><br>
                                </span>
                                </div>
                                `
                          }

                          this.tag = resData.data.totalElements > 5 ? repTag + `<div class="pl-5 ml-md-3 pl-md-3 ml-lg-5 pl-lg-5"><a href="javascript:void(0)" v-on:click="moreReply(` + 1 + `)" v-if="ifflag">댓글 더보기</a></div>` : repTag
                       }).catch(error => {
                          console.log(error)
                       })
                 } else {
                    repData[i - 1].ifflag = !repData[i - 1].ifflag
                 }

              },
              boardHeart: function () {
                 axios.post("http://127.0.0.1:8000/plusBoardHeart?nickname=" + "유저닉네임" + "&id=" + boardArray[i - 1].id)
                    .then(resData => {
                       this.boardHeartNum = resData.data
                    }).catch(error => {
                       console.log(error)
                    })
              },
              makeClaim: function () {
                 axios.post("http://127.0.0.1:8000/plusBoardClaim?nickname=" + "유저닉네임" + "&id=" + boardArray[i - 1].id)
                    .then(resData => {
                       alert(resData.data)
                    }).catch(error => {
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
     }

     // 페이징 처리 이벤트 추가
     $(document).on("click", "#topheader .pagination a", function () {
        $("#topheader .pagination").find("li.active").removeClass("active");
        $(this).parent("li").addClass("active");
     });

     // 페이지 로딩시 바로 실행되는 로직
     function getAxios() {
        axios.get("http://127.0.0.1:8000/getCount")
           .then(resData => {
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